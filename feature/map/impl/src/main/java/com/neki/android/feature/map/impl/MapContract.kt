package com.neki.android.feature.map.impl

data class MapState(
    val isLoading: Boolean = false,
    val dragState: DragValue = DragValue.Bottom,
    val isShowInfoDialog: Boolean = false,
)

sealed interface MapIntent {
    data object EnterMapScreen : MapIntent

    // in 패널
    data object ClickCurrentLocation : MapIntent
    data object ClickInfoIcon : MapIntent
    data object ClickCloseInfoIcon : MapIntent
    data object ClickToMapChip : MapIntent
    data object ClickBrand : MapIntent
    data object ClickNearBrand : MapIntent
    data object ClickCloseBrandCard : MapIntent
    data class ChangeDragValue(val dragValue: DragValue) : MapIntent
}

sealed interface MapEffect {
    data object RefreshCurrentLocation : MapEffect
    data class ShowToastMessage(val message: String) : MapEffect
}

enum class DragValue { Bottom, Center, Top, Invisible }
