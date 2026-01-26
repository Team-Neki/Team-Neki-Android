package com.neki.android.feature.map.impl

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.request.allowHardware
import coil3.toBitmap
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.common.permission.navigateToAppSettings
import com.neki.android.core.designsystem.dialog.SingleButtonAlertDialog
import com.neki.android.core.designsystem.dialog.WarningDialog
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.map.impl.component.AnchoredDraggablePanel
import com.neki.android.feature.map.impl.component.DirectionBottomSheet
import com.neki.android.feature.map.impl.component.MapRefreshChip
import com.neki.android.feature.map.impl.const.MapConst
import com.neki.android.feature.map.impl.component.PhotoBoothDetailCard
import com.neki.android.feature.map.impl.component.PhotoBoothMarker
import com.neki.android.feature.map.impl.component.ToMapChip
import com.neki.android.feature.map.impl.const.DirectionApp
import com.neki.android.feature.map.impl.util.DirectionHelper
import com.neki.android.feature.map.impl.util.getPlaceName
import kotlinx.coroutines.launch

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val coroutineScope = rememberCoroutineScope()

    var locationTrackingMode by remember { mutableStateOf(LocationTrackingMode.None) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(MapConst.DEFAULT_LATITUDE, MapConst.DEFAULT_LONGITUDE), MapConst.DEFAULT_ZOOM_LEVEL)
    }

    var previousShouldShowRationale by remember { mutableStateOf(false) }
    var isNavigatedToSettings by remember { mutableStateOf(false) }

    // 브랜드 이미지 Bitmap 캐시 (imageUrl -> ImageBitmap)
    val brandImageCache = remember { mutableStateMapOf<String, ImageBitmap>() }
    val nekiToast = remember { NekiToast(context) }

    // brands가 로드되면 이미지를 Bitmap으로 미리 로드
    LaunchedEffect(uiState.brands) {
        uiState.brands.forEach { brand ->
            if (brand.imageUrl.isNotEmpty() && !brandImageCache.containsKey(brand.imageUrl)) {
                val request = ImageRequest.Builder(context)
                    .data(brand.imageUrl)
                    .allowHardware(false)
                    .build()
                val result = context.imageLoader.execute(request)
                if (result is SuccessResult) {
                    brandImageCache[brand.imageUrl] = result.image.toBitmap().asImageBitmap()
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        val currentShouldShowRationale = LocationPermissionManager.shouldShowLocationRationale(activity)

        if (isGranted) {
            locationTrackingMode = LocationTrackingMode.Follow
        } else {
            if (!currentShouldShowRationale && !previousShouldShowRationale) {
                // 2회 이상 거부
                viewModel.store.onIntent(MapIntent.ShowLocationPermissionDialog)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        if (isNavigatedToSettings) {
            if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                locationTrackingMode = LocationTrackingMode.Follow
            }
            isNavigatedToSettings = false
        }
        onPauseOrDispose { }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is MapEffect.RefreshCurrentLocation -> {
                if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                    locationTrackingMode = LocationTrackingMode.Follow
                } else {
                    viewModel.store.onIntent(MapIntent.RequestLocationPermission)
                }
            }

            is MapEffect.OpenDirectionBottomSheet -> {
                if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                    viewModel.store.onIntent(MapIntent.OpenDirectionBottomSheet)
                } else {
                    viewModel.store.onIntent(MapIntent.RequestLocationPermission)
                }
            }
            is MapEffect.ShowToastMessage -> {
                nekiToast.showToast(sideEffect.message)
            }
            is MapEffect.MoveCameraToPosition -> {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdate.scrollAndZoomTo(
                            LatLng(sideEffect.latitude, sideEffect.longitude),
                            MapConst.DEFAULT_ZOOM_LEVEL,
                        ),
                        animation = CameraAnimation.Easing,
                        durationMs = 800,
                    )
                }
            }
            is MapEffect.MoveDirectionApp -> {
                val startLatitude = sideEffect.startLatitude
                val startLongitude = sideEffect.startLongitude
                val endLatitude = sideEffect.endLatitude
                val endLongitude = sideEffect.endLongitude

                when (sideEffect.app) {
                    DirectionApp.GOOGLE_MAP -> {
                        DirectionHelper.moveAppOrStore(
                            context = context,
                            url = "google.navigation:q=$endLatitude,$endLongitude&mode=w",
                            packageName = sideEffect.app.packageName,
                        )
                    }

                    DirectionApp.NAVER_MAP -> {
                        val startName = context.getPlaceName(startLatitude, startLongitude, "출발지")
                        val destName = context.getPlaceName(endLatitude, endLongitude, "도착지")
                        DirectionHelper.moveAppOrStore(
                            context = context,
                            url = "nmap://route/walk?slat=$startLatitude&slng=$startLongitude&sname=$startName&dlat=$endLatitude&dlng=$endLongitude&dname=$destName",
                            packageName = sideEffect.app.packageName,
                        )
                    }

                    DirectionApp.KAKAO_MAP -> {
                        DirectionHelper.moveAppOrStore(
                            context = context,
                            url = "kakaomap://route?sp=$startLatitude,$startLongitude&ep=$endLatitude,$endLongitude&by=FOOT",
                            packageName = sideEffect.app.packageName,
                        )
                    }
                }
            }

            is MapEffect.NavigateToAppSettings -> {
                isNavigatedToSettings = true
                navigateToAppSettings(context)
            }

            is MapEffect.RequestLocationPermission -> {
                previousShouldShowRationale = LocationPermissionManager.shouldShowLocationRationale(activity)
                locationPermissionLauncher.launch(LocationPermissionManager.LOCATION_PERMISSIONS)
            }
        }
    }

    MapScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
        locationTrackingMode = locationTrackingMode,
        onLocationTrackingModeChange = { locationTrackingMode = it },
        cameraPositionState = cameraPositionState,
        brandImageCache = brandImageCache,
        onMapLoaded = {
            val isGrantedPermission = LocationPermissionManager.isGrantedLocationPermission(context)
            if (isGrantedPermission) {
                locationTrackingMode = LocationTrackingMode.Follow
            } else {
                viewModel.store.onIntent(MapIntent.RequestLocationPermission)
            }
            cameraPositionState.contentBounds?.let { bounds ->
                viewModel.store.onIntent(
                    MapIntent.ClickRefreshButton(
                        MapBounds(
                            southWest = Location(bounds.southWest.latitude, bounds.southWest.longitude),
                            northWest = Location(bounds.northWest.latitude, bounds.northWest.longitude),
                            northEast = Location(bounds.northEast.latitude, bounds.northEast.longitude),
                            southEast = Location(bounds.southEast.latitude, bounds.southEast.longitude),
                        ),
                    ),
                )
            }
        },
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    uiState: MapState = MapState(),
    onIntent: (MapIntent) -> Unit = {},
    locationTrackingMode: LocationTrackingMode = LocationTrackingMode.None,
    onLocationTrackingModeChange: (LocationTrackingMode) -> Unit = {},
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(MapConst.DEFAULT_LATITUDE, MapConst.DEFAULT_LONGITUDE), MapConst.DEFAULT_ZOOM_LEVEL)
    },
    brandImageCache: Map<String, ImageBitmap> = emptyMap(),
    onMapLoaded: () -> Unit = {},
) {
    val mapProperties = remember(locationTrackingMode) {
        MapProperties(
            locationTrackingMode = locationTrackingMode,
        )
    }
    val mapUiSettings = remember {
        MapUiSettings(
            isLocationButtonEnabled = false,
            isZoomControlEnabled = false,
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            locationSource = rememberFusedLocationSource(),
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapLoaded = onMapLoaded,
            onOptionChange = {
                cameraPositionState.locationTrackingMode?.let {
                    onLocationTrackingModeChange(it)
                }
            },
            onLocationChange = { location ->
                onIntent(MapIntent.UpdateCurrentLocation(location.latitude, location.longitude))
            },
        ) {
            uiState.mapMarkers.forEach { photoBooth ->
                val cachedBitmap = brandImageCache[photoBooth.imageUrl]
                PhotoBoothMarker(
                    photoBooth = photoBooth,
                    isFocused = photoBooth.isFocused,
                    cachedBitmap = cachedBitmap,
                    onClick = {
                        onIntent(MapIntent.ClickPhotoBoothMarker(latitude = photoBooth.latitude, longitude = photoBooth.longitude))
                    },
                )
            }
        }

        AnchoredDraggablePanel(
            brands = uiState.brands,
            nearbyBrands = uiState.nearbyPhotoBooths,
            dragLevel = uiState.dragLevel,
            onDragLevelChanged = { onIntent(MapIntent.ChangeDragLevel(it)) },
            onClickInfoIcon = { onIntent(MapIntent.ClickInfoIcon) },
            isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
            onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocation) },
            onClickBrand = { onIntent(MapIntent.ClickBrand(it)) },
            onClickNearBrand = { onIntent(MapIntent.ClickNearPhotoBooth(it)) },
        )

        if ((uiState.dragLevel == DragLevel.FIRST || uiState.dragLevel == DragLevel.SECOND) &&
            locationTrackingMode != LocationTrackingMode.Follow
        ) {
            MapRefreshChip(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp),
                onClick = {
                    cameraPositionState.contentBounds?.let { bounds ->
                        onIntent(
                            MapIntent.ClickRefreshButton(
                                MapBounds(
                                    southWest = Location(bounds.southWest.latitude, bounds.southWest.longitude),
                                    northWest = Location(bounds.northWest.latitude, bounds.northWest.longitude),
                                    northEast = Location(bounds.northEast.latitude, bounds.northEast.longitude),
                                    southEast = Location(bounds.southEast.latitude, bounds.southEast.longitude),
                                ),
                            ),
                        )
                    }
                },
            )
        }

        if (uiState.dragLevel == DragLevel.THIRD) {
            ToMapChip(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                onClick = { onIntent(MapIntent.ClickToMapChip) },
            )
        } else if (uiState.dragLevel == DragLevel.INVISIBLE) {
            uiState.mapMarkers.find { it.isFocused }?.let { focusedPhotoBooth ->
                PhotoBoothDetailCard(
                    photoBooth = focusedPhotoBooth,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
                    onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocation) },
                    onClickCloseCard = { onIntent(MapIntent.ClickClosePhotoBoothCard) },
                    onClickCard = {
                        onIntent(MapIntent.ClickPhotoBoothMarker(focusedPhotoBooth.latitude, focusedPhotoBooth.longitude))
                    },
                    onClickDirection = { onIntent(MapIntent.ClickDirection) },
                )
            }
        }
    }

    if (uiState.isShowInfoDialog) {
        WarningDialog(
            content = "가까운 네컷 사진 브랜드는\n1km 기준으로 표시돼요.",
            onDismissRequest = { onIntent(MapIntent.ClickCloseInfoIcon) },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        )
    }

    if (uiState.isShowDirectionBottomSheet) {
        DirectionBottomSheet(
            onDismissRequest = { onIntent(MapIntent.CloseDirectionBottomSheet) },
            onClickDirectionItem = { onIntent(MapIntent.ClickDirectionItem(it)) },
        )
    }

    if (uiState.isShowLocationPermissionDialog) {
        SingleButtonAlertDialog(
            title = "위치 권한",
            content = "설정에서 위치 권한을 허용해주세요.",
            buttonText = "확인",
            onDismissRequest = { onIntent(MapIntent.DismissLocationPermissionDialog) },
            onClick = { onIntent(MapIntent.ConfirmLocationPermissionDialog) },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        )
    }

    val isLoadingLocation = locationTrackingMode == LocationTrackingMode.Follow && uiState.currentLocation == null

    if (isLoadingLocation || uiState.isLoading) {
        LoadingDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }
}
