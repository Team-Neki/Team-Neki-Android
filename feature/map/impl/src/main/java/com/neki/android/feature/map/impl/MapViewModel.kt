package com.neki.android.feature.map.impl

import androidx.lifecycle.ViewModel
import com.naver.maps.map.compose.MapEffect
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    val store: MviIntentStore<MapState, MapIntent, MapEffect> =
        mviIntentStore(
            initialState = MapState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: MapIntent,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        when (intent) {
            MapIntent.EnterMapScreen -> {}
            MapIntent.ClickCurrentLocation -> { postSideEffect(MapEffect.RefreshCurrentLocation) }
            MapIntent.ClickInfoIcon -> { reduce { copy(isShowInfoDialog = true) } }
            MapIntent.ClickCloseInfoIcon -> { reduce { copy(isShowInfoDialog = false) } }
            MapIntent.ClickToMapChip -> { reduce { copy(dragState = DragValue.Bottom) } }
            MapIntent.ClickBrand -> { }
            MapIntent.ClickNearBrand -> { reduce { copy(dragState = DragValue.Invisible) } }
            MapIntent.ClickCloseBrandCard -> { reduce { copy(dragState = DragValue.Center) } }
            MapIntent.CloseDirectionBottomSheet -> { reduce { copy(isShowDirectionBottomSheet = false) } }
            is MapIntent.ClickDirectionItem -> {
                postSideEffect(MapEffect.MoveDirectionApp(intent.app))
            }
            is MapIntent.ChangeDragValue -> { reduce { copy(dragState = intent.dragValue) } }
            is MapIntent.ClickBrandMarker -> {
                reduce { copy(dragState = DragValue.Invisible) }
                postSideEffect(MapEffect.MoveCameraToPosition(intent.latitude, intent.longitude))
            }
            is MapIntent.ClickDirection -> {
                reduce { copy(isShowDirectionBottomSheet = true) }
            }
        }
    }

}
