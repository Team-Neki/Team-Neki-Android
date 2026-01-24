package com.neki.android.feature.map.impl

import com.neki.android.core.model.Brand
import com.neki.android.core.model.BrandInfo
import com.neki.android.feature.map.impl.const.DirectionApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MapState(
    val isLoading: Boolean = false,
    val currentLocation: Pair<Double, Double>? = null,
    val dragLevel: DragLevel = DragLevel.FIRST,
    val brands: ImmutableList<Brand> = persistentListOf(),
    val nearbyPhotoBooths: ImmutableList<BrandInfo> = persistentListOf(),
    val focusedMarkerPosition: Pair<Double, Double>? = null,
    val selectedPhotoBoothInfo: BrandInfo? = null,
    val isShowInfoDialog: Boolean = false,
    val isShowDirectionBottomSheet: Boolean = false,
    val isShowLocationPermissionDialog: Boolean = false,
)

sealed interface MapIntent {
    data object EnterMapScreen : MapIntent

    // in 지도
    data class ClickPhotoBoothMarker(
        val latitude: Double,
        val longitude: Double,
    ) : MapIntent
    data class ClickDirection(
        val latitude: Double,
        val longitude: Double,
    ) : MapIntent
    data object ClickRefresh : MapIntent
    data class UpdateCurrentLocation(val latitude: Double, val longitude: Double) : MapIntent

    // in 패널
    data object ClickCurrentLocation : MapIntent
    data object ClickInfoIcon : MapIntent
    data object ClickCloseInfoIcon : MapIntent
    data object ClickToMapChip : MapIntent
    data class ClickBrand(val brand: Brand) : MapIntent
    data class ClickNearPhotoBooth(val brandInfo: BrandInfo) : MapIntent
    data object ClickClosePhotoBoothCard : MapIntent
    data object CloseDirectionBottomSheet : MapIntent
    data class ClickDirectionItem(val app: DirectionApp) : MapIntent
    data class ChangeDragLevel(val dragLevel: DragLevel) : MapIntent

    // 위치 권한
    data class RequestLocationPermission(val shouldShowRationale: Boolean) : MapIntent
    data object DismissLocationPermissionDialog : MapIntent
    data object ConfirmLocationPermissionDialog : MapIntent
}

sealed interface MapEffect {
    data object RefreshPhotoBooth : MapEffect
    data object RefreshCurrentLocation : MapEffect
    data class ShowToastMessage(val message: String) : MapEffect
    data class MoveCameraToPosition(val latitude: Double, val longitude: Double) : MapEffect

    data class MoveDirectionApp(
        val app: DirectionApp,
        val startLatitude: Double,
        val startLongitude: Double,
        val endLatitude: Double,
        val endLongitude: Double,
    ) : MapEffect

    data object NavigateToAppSettings : MapEffect
    data object RequestLocationPermission : MapEffect
}

enum class DragLevel { FIRST, SECOND, THIRD, INVISIBLE }
