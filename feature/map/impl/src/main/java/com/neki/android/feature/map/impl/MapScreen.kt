package com.neki.android.feature.map.impl

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.dialog.WarningDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.map.impl.component.AnchoredDraggablePanel
import com.neki.android.feature.map.impl.component.BrandMarker
import com.neki.android.feature.map.impl.component.DirectionBottomSheet
import com.neki.android.feature.map.impl.component.PanelInvisibleContent
import com.neki.android.feature.map.impl.component.ToMapChip
import com.neki.android.feature.map.impl.const.DirectionAppConst
import com.neki.android.feature.map.impl.util.DirectionHelper
import com.neki.android.feature.map.impl.util.getPlaceName

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
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
                            17.0
                        ),
                        animation = CameraAnimation.Easing,
                        durationMs = 800
                    )
                }
            }
            is MapEffect.MoveDirectionApp -> {
                when (sideEffect.app) {
                    DirectionAppConst.GOOGLE_MAP -> {
                        DirectionHelper().moveAppOrStore(
                            context = context,
                            url = "google.navigation:q=37.5256372,126.8862648(${context.getPlaceName(37.5256372, 126.8861924, "도착지")})&mode=w",
                            packageName = sideEffect.app.packageName
                        )
                    }

                    DirectionAppConst.NAVER_MAP -> {
                        val startName = context.getPlaceName(37.5270539, 126.8862648, "출발지")
                        val destName = context.getPlaceName(37.5256372, 126.8861924, "도착지")
                        DirectionHelper().moveAppOrStore(
                            context = context,
                            url = "nmap://route/walk?slat=37.5270539&slng=126.8862648&sname=$startName&dlat=37.5256372&dlng=126.8861924&dname=$destName",
                            packageName = sideEffect.app.packageName
                        )
                    }

                    DirectionAppConst.KAKAO_MAP -> {
                        DirectionHelper().moveAppOrStore(
                            context = context,
                            url = "kakaomap://route?sp=37.5270539,126.8862648&ep=37.5256372,126.8861924&by=FOOT",
                            packageName = sideEffect.app.packageName
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

    if (uiState.isShowInfoDialog) {
        WarningDialog(
            content = "가까운 네컷 사진 브랜드는\n1km 기준으로 표시돼요.",
            onDismissRequest = { viewModel.store.onIntent(MapIntent.ClickCloseInfoIcon) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }

    if (uiState.isShowDirectionBottomSheet) {
        DirectionBottomSheet(
            onDismissRequest = { viewModel.store.onIntent(MapIntent.CloseDirectionBottomSheet) },
            onClickDirectionItem = { viewModel.store.onIntent(MapIntent.ClickDirectionItem(it)) }
        )
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
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
            locationTrackingMode = locationTrackingMode
        )
    }
    val mapUiSettings = remember {
        MapUiSettings(
            isLocationButtonEnabled = false,
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
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
            }
        ) {
            // 인생네컷 (중심에서 북쪽 30m)
            BrandMarker(
                key = arrayOf("life_four_cut"),
                latitude = 37.5272,
                longitude = 126.8862,
                brandImageRes = R.drawable.icon_life_four_cut,
                isFocused = uiState.focusedMarkerPosition == (37.5272 to 126.8862),
                onClick = {
                    onIntent(MapIntent.ClickBrandMarker(latitude = 37.5272, longitude = 126.8862))
                }
            )

            // 포토그레이 (중심에서 동쪽 50m)
            BrandMarker(
                key = arrayOf("photogray"),
                latitude = 37.5269,
                longitude = 126.8868,
                brandImageRes = R.drawable.icon_photogray,
                isFocused = uiState.focusedMarkerPosition == (37.5269 to 126.8868),
                onClick = {
                    onIntent(MapIntent.ClickBrandMarker(latitude = 37.5269, longitude = 126.8868))
                }
            )

            // 포토이즘 (중심에서 남동쪽 60m)
            BrandMarker(
                key = arrayOf("photoism"),
                latitude = 37.5265,
                longitude = 126.8867,
                brandImageRes = R.drawable.icon_photoism,
                isFocused = uiState.focusedMarkerPosition == (37.5265 to 126.8867),
                onClick = {
                    onIntent(MapIntent.ClickBrandMarker(latitude = 37.5265, longitude = 126.8867))
                }
            )

            // 하루필름 (중심에서 서쪽 40m)
            BrandMarker(
                key = arrayOf("haru_film"),
                latitude = 37.5270,
                longitude = 126.8857,
                brandImageRes = R.drawable.icon_haru_film,
                isFocused = uiState.focusedMarkerPosition == (37.5270 to 126.8857),
                onClick = {
                    onIntent(MapIntent.ClickBrandMarker(latitude = 37.5270, longitude = 126.8857))
                }
            )

            // 플랜비스튜디오 (중심에서 북서쪽 70m)
            BrandMarker(
                key = arrayOf("planb_studio"),
                latitude = 37.5274,
                longitude = 126.8855,
                brandImageRes = R.drawable.icon_planb_studio,
                isFocused = uiState.focusedMarkerPosition == (37.5274 to 126.8855),
                onClick = {
                    onIntent(MapIntent.ClickBrandMarker(latitude = 37.5274, longitude = 126.8855))
                }
            )

            // 포토시그니처 (중심에서 남쪽 80m)
            BrandMarker(
                key = arrayOf("photo_signature"),
                latitude = 37.5262,
                longitude = 126.8861,
                brandImageRes = R.drawable.icon_photo_signature,
                isFocused = uiState.focusedMarkerPosition == (37.5262 to 126.8861),
                onClick = {
                    onIntent(MapIntent.ClickBrandMarker(latitude = 37.5262, longitude = 126.8861))
                }
            )
        }

        AnchoredDraggablePanel(
            brands = uiState.brands,
            nearbyBrands = uiState.nearbyBrands,
            dragValue = uiState.dragState,
            onDragValueChanged = { onIntent(MapIntent.ChangeDragValue(it)) },
            onClickInfoIcon = { onIntent(MapIntent.ClickInfoIcon) },
            isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
            onClickCurrentLocation = {
                onLocationTrackingModeChange(LocationTrackingMode.Follow)
            },
            onClickBrand = { onIntent(MapIntent.ClickBrand) },
            onClickNearBrand = { onIntent(MapIntent.ClickNearBrand) }
        )

        if (uiState.dragState == DragValue.Top) {
            ToMapChip(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                onClick = { onIntent(MapIntent.ClickToMapChip) }
            )
        } else if (uiState.dragState == DragValue.Invisible && uiState.nearbyBrands.isNotEmpty()) {
            PanelInvisibleContent(
                brandInfo = uiState.nearbyBrands.first(),
                modifier = Modifier.align(Alignment.BottomCenter),
                isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
                onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocation) },
                onClickCloseCard = { onIntent(MapIntent.ClickCloseBrandCard) },
                onClickDirection = { onIntent(MapIntent.ClickDirection(37.5256372, 126.8861924)) }
            )
        }
    }
}

@Preview
@Composable
private fun MapScreenPreview() {
    NekiTheme {
        MapScreen()
    }
}
