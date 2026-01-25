package com.neki.android.feature.map.impl

import android.widget.Toast
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
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.neki.android.core.designsystem.dialog.SingleButtonAlertDialog
import com.neki.android.core.designsystem.dialog.WarningDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.map.impl.component.AnchoredDraggablePanel
import com.neki.android.feature.map.impl.component.PhotoBoothMarker
import com.neki.android.feature.map.impl.component.DirectionBottomSheet
import com.neki.android.feature.map.impl.component.MapRefreshChip
import com.neki.android.feature.map.impl.component.PhotoBoothDetailCard
import com.neki.android.feature.map.impl.component.ToMapChip
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.common.permission.navigateToAppSettings
import com.neki.android.feature.map.impl.const.DirectionApp
import com.neki.android.feature.map.impl.util.DirectionHelper
import com.neki.android.feature.map.impl.util.getPlaceName

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
        position = CameraPosition(LatLng(37.5269278, 126.886225), 17.0)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        if (isGranted) {
            locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    LaunchedEffect(Unit) {
        if (!LocationPermissionManager.hasLocationPermission(context)) {
            viewModel.store.onIntent(MapIntent.RequestLocationPermission(LocationPermissionManager.shouldShowLocationRationale(activity)))
        }
        viewModel.store.onIntent(MapIntent.EnterMapScreen)
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is MapEffect.RefreshPhotoBooth -> {
                // TODO: 포토부스 새로고침 로직 구현
            }
            is MapEffect.RefreshCurrentLocation -> {
                locationTrackingMode = LocationTrackingMode.Follow
            }
            is MapEffect.ShowToastMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is MapEffect.MoveCameraToPosition -> {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdate.scrollAndZoomTo(
                            LatLng(sideEffect.latitude, sideEffect.longitude),
                            17.0,
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
                navigateToAppSettings(context)
            }

            is MapEffect.RequestLocationPermission -> {
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
        position = CameraPosition(LatLng(37.5269278, 126.886225), 17.0)
    },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

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
            onOptionChange = {
                cameraPositionState.locationTrackingMode?.let {
                    onLocationTrackingModeChange(it)
                }
            },
            onLocationChange = { location ->
                onIntent(MapIntent.UpdateCurrentLocation(location.latitude, location.longitude))
            },
        ) {
            uiState.nearbyPhotoBooths.forEachIndexed { index, brandInfo ->
                val isFocused = uiState.focusedMarkerPosition == (brandInfo.latitude to brandInfo.longitude)
                PhotoBoothMarker(
                    keys = arrayOf("$isFocused"),
                    latitude = brandInfo.latitude,
                    longitude = brandInfo.longitude,
                    brandImageRes = brandInfo.brandImageRes,
                    isFocused = isFocused,
                    onClick = {
                        onIntent(MapIntent.ClickPhotoBoothMarker(latitude = brandInfo.latitude, longitude = brandInfo.longitude))
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
            onClickCurrentLocation = {
                if (LocationPermissionManager.hasLocationPermission(context)) {
                    onLocationTrackingModeChange(LocationTrackingMode.Follow)
                } else {
                    onIntent(MapIntent.RequestLocationPermission(LocationPermissionManager.shouldShowLocationRationale(activity)))
                }
            },
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
                onClick = { onIntent(MapIntent.ClickRefresh) },
            )
        }

        if (uiState.dragLevel == DragLevel.THIRD) {
            ToMapChip(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                onClick = { onIntent(MapIntent.ClickToMapChip) },
            )
        } else if (uiState.dragLevel == DragLevel.INVISIBLE && uiState.selectedPhotoBoothInfo != null) {
            PhotoBoothDetailCard(
                brandInfo = uiState.selectedPhotoBoothInfo,
                modifier = Modifier.align(Alignment.BottomCenter),
                isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
                onClickCurrentLocation = {
                    if (LocationPermissionManager.hasLocationPermission(context)) {
                        onIntent(MapIntent.ClickCurrentLocation)
                    } else {
                        onIntent(MapIntent.RequestLocationPermission(LocationPermissionManager.shouldShowLocationRationale(activity)))
                    }
                },
                onClickCloseCard = { onIntent(MapIntent.ClickClosePhotoBoothCard) },
                onClickDirection = {
                    if (LocationPermissionManager.hasLocationPermission(context)) {
                        onIntent(MapIntent.ClickDirection(uiState.selectedPhotoBoothInfo.latitude, uiState.selectedPhotoBoothInfo.longitude))
                    } else {
                        onIntent(MapIntent.RequestLocationPermission(LocationPermissionManager.shouldShowLocationRationale(activity)))
                    }
                },
            )
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
}
