package com.neki.android.feature.map.impl

import com.neki.android.core.model.Brand
import com.neki.android.core.model.BrandInfo
import com.neki.android.feature.map.impl.const.DirectionAppConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MapState(
    val isLoading: Boolean = false,
    val brands: ImmutableList<Brand> = persistentListOf(),
    val nearbyBrands: ImmutableList<BrandInfo> = persistentListOf(),
    val focusedMarkerPosition: Pair<Double, Double> = Pair(0.0, 0.0),
    val currentLocation: Pair<Double, Double> = Pair(0.0, 0.0),
    val dragState: DragValue = DragValue.Bottom,
    val isShowInfoDialog: Boolean = false,
    val isShowDirectionBottomSheet: Boolean = false,
)

sealed interface MapIntent {
    data object EnterMapScreen : MapIntent

    // in 지도
    data class ClickBrandMarker(
        val latitude: Double,
        val longitude: Double
    ) : MapIntent
    data class ClickDirection(
        val latitude: Double,
        val longitude: Double
    ) : MapIntent

    // in 패널
    data object ClickCurrentLocation : MapIntent
    data object ClickInfoIcon : MapIntent
    data object ClickCloseInfoIcon : MapIntent
    data object ClickToMapChip : MapIntent
    data object ClickBrand : MapIntent
    data object ClickNearBrand : MapIntent
    data object ClickCloseBrandCard : MapIntent
    data object CloseDirectionBottomSheet : MapIntent
    data class ClickDirectionItem(val app: DirectionAppConst) : MapIntent
    data class ChangeDragValue(val dragValue: DragValue) : MapIntent

}

sealed interface MapEffect {
    data object RefreshCurrentLocation : MapEffect
    data class ShowToastMessage(val message: String) : MapEffect
    data class MoveCameraToPosition(
        val latitude: Double,
        val longitude: Double
    ) : MapEffect
    data class MoveDirectionApp(
        val app: DirectionAppConst,
    ) : MapEffect
}

enum class DragValue { Bottom, Center, Top, Invisible }
