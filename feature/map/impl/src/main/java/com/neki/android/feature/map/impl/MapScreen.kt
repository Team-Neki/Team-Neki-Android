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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CameraUpdateReason
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
import com.neki.android.feature.map.impl.component.PhotoBoothDetailCard
import com.neki.android.feature.map.impl.component.PhotoBoothMarker
import com.neki.android.feature.map.impl.component.ToMapChip
import com.neki.android.feature.map.impl.const.MapConst
import com.neki.android.feature.map.impl.util.DirectionHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val scope = rememberCoroutineScope()
    val nekiToast = remember { NekiToast(context) }

    var locationTrackingMode by remember { mutableStateOf(LocationTrackingMode.None) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(MapConst.DEFAULT_LATITUDE, MapConst.DEFAULT_LONGITUDE),
            MapConst.DEFAULT_ZOOM_LEVEL,
        )
    }

    var previousShouldShowRationale by remember { mutableStateOf(false) }
    var isNavigatedToSettings by remember { mutableStateOf(false) }
    var isFirstLocationLoaded by remember { mutableStateOf(false) }

    fun enableFollowMode() {
        if (locationTrackingMode != LocationTrackingMode.Follow) {
            cameraPositionState.move(CameraUpdate.zoomTo(MapConst.DEFAULT_ZOOM_LEVEL))
            locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    // 브랜드 이미지 Bitmap 캐시 (imageUrl -> ImageBitmap)
    val brandImageCache = remember { mutableStateMapOf<String, ImageBitmap>() }

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

    LaunchedEffect(cameraPositionState.cameraUpdateReason, cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving && cameraPositionState.cameraUpdateReason == CameraUpdateReason.LOCATION && !isFirstLocationLoaded) {
            isFirstLocationLoaded = true
            // 권한 있고, 최초 요청 시
            cameraPositionState.contentBounds?.let { bounds ->
                viewModel.store.onIntent(
                    MapIntent.LoadPhotoBoothsByBounds(
                        MapBounds(
                            southWest = LocLatLng(bounds.southWest.latitude, bounds.southWest.longitude),
                            northWest = LocLatLng(bounds.northWest.latitude, bounds.northWest.longitude),
                            northEast = LocLatLng(bounds.northEast.latitude, bounds.northEast.longitude),
                            southEast = LocLatLng(bounds.southEast.latitude, bounds.southEast.longitude),
                        ),
                    ),
                )
            }
        } else if (!cameraPositionState.isMoving && cameraPositionState.cameraUpdateReason == CameraUpdateReason.NO_MOVEMENT_YET) {
            // 권한 없음 → 강남역 기본 위치
            cameraPositionState.contentBounds?.let { bounds ->
                viewModel.store.onIntent(
                    MapIntent.LoadPhotoBoothsByBounds(
                        MapBounds(
                            southWest = LocLatLng(bounds.southWest.latitude, bounds.southWest.longitude),
                            northWest = LocLatLng(bounds.northWest.latitude, bounds.northWest.longitude),
                            northEast = LocLatLng(bounds.northEast.latitude, bounds.northEast.longitude),
                            southEast = LocLatLng(bounds.southEast.latitude, bounds.southEast.longitude),
                        ),
                    ),
                )
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        val currentShouldShowRationale = LocationPermissionManager.shouldShowLocationRationale(activity)

        if (isGranted) {
            enableFollowMode()
        } else {
            // 2회 이상 거부
            if (!currentShouldShowRationale && !previousShouldShowRationale) {
                viewModel.store.onIntent(MapIntent.ShowLocationPermissionDialog)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        if (isNavigatedToSettings) {
            if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                enableFollowMode()
            }
            isNavigatedToSettings = false
        }
        onPauseOrDispose { }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is MapEffect.TrackingFollowMode -> {
                if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                    enableFollowMode()
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
            is MapEffect.MoveCameraToPosition -> {
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdate.scrollAndZoomTo(
                            LatLng(sideEffect.locLatLng.latitude, sideEffect.locLatLng.longitude),
                            MapConst.DEFAULT_ZOOM_LEVEL,
                        ),
                        animation = CameraAnimation.Easing,
                        durationMs = MapConst.DEFAULT_CAMERA_ANIMATION_DURATIONS_MS,
                    )
                }
            }
            is MapEffect.LaunchDirectionApp -> {
                DirectionHelper.navigateToUrl(
                    context = context,
                    app = sideEffect.app,
                    startLatitude = sideEffect.startLocLatLng.latitude,
                    startLongitude = sideEffect.startLocLatLng.longitude,
                    endLatitude = sideEffect.endLocLatLng.latitude,
                    endLongitude = sideEffect.endLocLatLng.longitude,
                )
            }
            is MapEffect.NavigateToAppSettings -> {
                isNavigatedToSettings = true
                navigateToAppSettings(context)
            }
            is MapEffect.RequestLocationPermission -> {
                previousShouldShowRationale = LocationPermissionManager.shouldShowLocationRationale(activity)
                locationPermissionLauncher.launch(LocationPermissionManager.LOCATION_PERMISSIONS)
            }
            is MapEffect.ShowToastMessage -> nekiToast.showToast(sideEffect.message)
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
                enableFollowMode()
            } else {
                viewModel.store.onIntent(MapIntent.RequestLocationPermission)
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
                onIntent(MapIntent.UpdateCurrentLocation(LocLatLng(location.latitude, location.longitude)))
            },
        ) {
            uiState.mapMarkers
                .filter { it.isCheckedBrand }
                .forEach { photoBooth ->
                    val cachedBitmap = brandImageCache[photoBooth.imageUrl]
                    PhotoBoothMarker(
                        photoBooth = photoBooth,
                        isFocused = photoBooth.isFocused,
                        cachedBitmap = cachedBitmap,
                        onClick = {
                            onIntent(MapIntent.ClickPhotoBoothMarker(LocLatLng(photoBooth.latitude, photoBooth.longitude)))
                        },
                    )
                }
        }

        AnchoredDraggablePanel(
            brands = uiState.brands,
            nearbyPhotoBooths = uiState.nearbyPhotoBooths,
            dragLevel = uiState.dragLevel,
            onDragLevelChanged = { onIntent(MapIntent.ChangeDragLevel(it)) },
            onClickInfoIcon = { onIntent(MapIntent.ClickInfoIcon) },
            isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
            onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocation) },
            onClickBrand = { onIntent(MapIntent.ClickBrand(it)) },
            onClickNearPhotoBooth = { onIntent(MapIntent.ClickNearPhotoBooth(it)) },
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
                                    southWest = LocLatLng(bounds.southWest.latitude, bounds.southWest.longitude),
                                    northWest = LocLatLng(bounds.northWest.latitude, bounds.northWest.longitude),
                                    northEast = LocLatLng(bounds.northEast.latitude, bounds.northEast.longitude),
                                    southEast = LocLatLng(bounds.southEast.latitude, bounds.southEast.longitude),
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
                        onIntent(MapIntent.ClickPhotoBoothCard(LocLatLng(focusedPhotoBooth.latitude, focusedPhotoBooth.longitude)))
                    },
                    onClickDirection = { onIntent(MapIntent.ClickDirectionIcon) },
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

    val isLoadingLocation = locationTrackingMode == LocationTrackingMode.Follow && uiState.currentLocLatLng == null

    if (isLoadingLocation || uiState.isLoading) {
        LoadingDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }
}
