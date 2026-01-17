package com.neki.android.feature.map.impl

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
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
import com.neki.android.feature.map.impl.component.PanelInvisibleContent
import com.neki.android.feature.map.impl.component.ToMapChip

@Composable
fun MapRoute(
    viewModel: MapViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var locationTrackingMode by remember { mutableStateOf(LocationTrackingMode.None) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is MapEffect.RefreshCurrentLocation -> {
                locationTrackingMode = LocationTrackingMode.Follow
            }
            is MapEffect.ShowToastMessage -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    MapScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
        locationTrackingMode = locationTrackingMode,
        onLocationTrackingModeChange = { locationTrackingMode = it },
    )

    if (uiState.isShowInfoDialog) {
        WarningDialog(
            content = "가까운 네컷 사진 브랜드는\n1km 기준으로 표시돼요.",
            onDismissRequest = { viewModel.store.onIntent(MapIntent.ClickCloseInfoIcon) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
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
) {
    val mapProperties = remember(locationTrackingMode) {
        MapProperties(
            locationTrackingMode = locationTrackingMode
        )
    }
    val mapUiSettings = remember {
        MapUiSettings(
            isLocationButtonEnabled = true,
            isIndoorLevelPickerEnabled = false
        )
    }
    val cameraPositionState = rememberCameraPositionState {
        /** 초기 위치 강남역 설정 **/
        position = CameraPosition(LatLng(37.4979, 127.0276), 15.0)
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
        )

        AnchoredDraggablePanel(
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
        } else if (uiState.dragState == DragValue.Invisible) {
            PanelInvisibleContent(
                modifier = Modifier.align(Alignment.BottomCenter),
                isCurrentLocation = locationTrackingMode == LocationTrackingMode.Follow,
                onClickCurrentLocation = { onIntent(MapIntent.ClickCurrentLocation) },
                onClickCloseCard = { onIntent(MapIntent.ClickCloseBrandCard) },
                onClickDirection = {}
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
