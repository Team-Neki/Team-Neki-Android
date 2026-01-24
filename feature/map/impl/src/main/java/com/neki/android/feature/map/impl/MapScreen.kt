package com.neki.android.feature.map.impl

import android.widget.Toast
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.neki.android.core.designsystem.dialog.WarningDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.map.impl.component.AnchoredDraggablePanel
import com.neki.android.feature.map.impl.component.BrandMarker
import com.neki.android.feature.map.impl.component.DirectionBottomSheet
import com.neki.android.feature.map.impl.component.MapRefreshChip
import com.neki.android.feature.map.impl.component.PanelInvisibleContent
import com.neki.android.feature.map.impl.component.ToMapChip
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
    val coroutineScope = rememberCoroutineScope()
    var locationTrackingMode by remember { mutableStateOf(LocationTrackingMode.None) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(37.5269278, 126.886225), 17.0)
    }

    LaunchedEffect(Unit) {
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
                when (sideEffect.app) {
                    DirectionApp.GOOGLE_MAP -> {
                        DirectionHelper.moveAppOrStore(
                            context = context,
                            url = "google.navigation:q=37.5256372,126.8862648(${context.getPlaceName(37.5256372, 126.8861924, "도착지")})&mode=w",
                            packageName = sideEffect.app.packageName,
                        )
                    }

                    DirectionApp.NAVER_MAP -> {
                        val startName = context.getPlaceName(37.5270539, 126.8862648, "출발지")
                        val destName = context.getPlaceName(37.5256372, 126.8861924, "도착지")
                        DirectionHelper.moveAppOrStore(
                            context = context,
                            url = "nmap://route/walk?slat=37.5270539&slng=126.8862648&sname=$startName&dlat=37.5256372&dlng=126.8861924&dname=$destName",
                            packageName = sideEffect.app.packageName,
                        )
                    }

                    DirectionApp.KAKAO_MAP -> {
                        DirectionHelper.moveAppOrStore(
                            context = context,
                            url = "kakaomap://route?sp=37.5270539,126.8862648&ep=37.5256372,126.8861924&by=FOOT",
                            packageName = sideEffect.app.packageName,
                        )
                    }
                }
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
        ) {
            uiState.nearbyBrands.forEachIndexed { index, brandInfo ->
                val isFocused = uiState.focusedMarkerPosition == (brandInfo.latitude to brandInfo.longitude)
                BrandMarker(
                    keys = arrayOf("$isFocused"),
                    latitude = brandInfo.latitude,
                    longitude = brandInfo.longitude,
                    brandImageRes = brandInfo.brandImageRes,
                    isFocused = isFocused,
                    onClick = {
                        onIntent(MapIntent.ClickBrandMarker(latitude = brandInfo.latitude, longitude = brandInfo.longitude))
                    },
                )
            }
        }

        AnchoredDraggablePanel(
            brands = uiState.brands,
            nearbyBrands = uiState.nearbyBrands,
            dragLevel = uiState.dragLevel,
            onDragLevelChanged = { onIntent(MapIntent.ChangeDragLevel(it)) },
            onClickInfoIcon = { onIntent(MapIntent.ClickInfoIcon) },
            isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
            onClickCurrentLocation = {
                onLocationTrackingModeChange(LocationTrackingMode.Follow)
            },
            onClickBrand = { onIntent(MapIntent.ClickBrand(it)) },
            onClickNearBrand = { onIntent(MapIntent.ClickNearBrand(it)) },
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
        } else if (uiState.dragLevel == DragLevel.INVISIBLE && uiState.selectedBrandInfo != null) {
            PanelInvisibleContent(
                brandInfo = uiState.selectedBrandInfo,
                modifier = Modifier.align(Alignment.BottomCenter),
                isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
                onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocation) },
                onClickCloseCard = { onIntent(MapIntent.ClickCloseBrandCard) },
                onClickDirection = { onIntent(MapIntent.ClickDirection(uiState.selectedBrandInfo.latitude, uiState.selectedBrandInfo.longitude)) },
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
}

@Preview
@Composable
private fun MapScreenPreview() {
    NekiTheme {
        MapScreen()
    }
}
