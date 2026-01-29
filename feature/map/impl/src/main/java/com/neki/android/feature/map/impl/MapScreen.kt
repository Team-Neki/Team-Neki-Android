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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.neki.android.feature.map.impl.component.PhotoBoothDetailContent
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
    var isNavigatedToSettings by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isGranted = permissions.values.any { it }

        if (isGranted) {
            locationTrackingMode = LocationTrackingMode.NoFollow
            viewModel.store.onIntent(MapIntent.GrantedLocationPermission)
        } else {
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

            // 영구 거부
            if (!LocationPermissionManager.shouldShowLocationRationale(activity)) {
                viewModel.store.onIntent(MapIntent.ShowLocationPermissionDialog)
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.isMoving to cameraPositionState.cameraUpdateReason }
            .collect { (isMoving, reason) ->
                if (isMoving && reason == CameraUpdateReason.GESTURE) {
                    viewModel.store.onIntent(MapIntent.GestureOnMap)
                }
            }
    }

    LifecycleResumeEffect(Unit) {
        if (isNavigatedToSettings) {
            if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                locationTrackingMode = LocationTrackingMode.NoFollow
                viewModel.store.onIntent(MapIntent.GrantedLocationPermission)
            }
            isNavigatedToSettings = false
        }
        onPauseOrDispose { }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is MapEffect.OpenDirectionBottomSheet -> {
                viewModel.store.onIntent(MapIntent.OpenDirectionBottomSheet)
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

                    if (sideEffect.isRequiredLoadPhotoBooths) {
                        locationTrackingMode = LocationTrackingMode.NoFollow
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

            is MapEffect.LaunchLocationPermission -> {
                locationPermissionLauncher.launch(LocationPermissionManager.LOCATION_PERMISSIONS)
            }

            is MapEffect.ShowToastMessage -> nekiToast.showToast(sideEffect.message)
        }
    }

    MapScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
        locationTrackingMode = locationTrackingMode,
        cameraPositionState = cameraPositionState,
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    uiState: MapState = MapState(),
    onIntent: (MapIntent) -> Unit = {},
    locationTrackingMode: LocationTrackingMode = LocationTrackingMode.None,
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(MapConst.DEFAULT_LATITUDE, MapConst.DEFAULT_LONGITUDE), MapConst.DEFAULT_ZOOM_LEVEL)
    },
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
            onMapLoaded = { onIntent(MapIntent.RequestLocationPermission) },
            onLocationChange = { location ->
                onIntent(MapIntent.UpdateCurrentLocation(LocLatLng(location.latitude, location.longitude)))
            },
        ) {
            uiState.mapMarkers.filter { it.isCheckedBrand }.forEach { photoBooth ->
                PhotoBoothMarker(
                    photoBooth = photoBooth,
                    cachedBitmap = uiState.brandImageCache[photoBooth.imageUrl],
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
            isCurrentLocation = uiState.isCameraOnCurrentLocation,
            onClickInfoIcon = { onIntent(MapIntent.ClickInfoIcon) },
            onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocationIcon) },
            onClickBrand = { onIntent(MapIntent.ClickVerticalBrand(it)) },
            onClickNearPhotoBooth = { onIntent(MapIntent.ClickNearPhotoBooth(it)) },
        )

        if ((uiState.dragLevel == DragLevel.FIRST || uiState.dragLevel == DragLevel.SECOND) && uiState.isVisibleRefreshButton) {
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
                PhotoBoothDetailContent(
                    photoBooth = focusedPhotoBooth,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    isCurrentLocation = uiState.isCameraOnCurrentLocation,
                    onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocationIcon) },
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

    if (uiState.isLoading) {
        LoadingDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        )
    }
}
