package com.neki.android.feature.map.impl

import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.const.DirectionApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MapState(
    val isLoading: Boolean = false,
    val currentLocation: Pair<Double, Double>? = null,
    val mapBounds: MapBounds? = null,
    val dragLevel: DragLevel = DragLevel.FIRST,
    val brands: ImmutableList<Brand> = persistentListOf(),
    val mapMarkers: ImmutableList<PhotoBooth> = persistentListOf(),
    val nearbyPhotoBooths: ImmutableList<PhotoBooth> = persistentListOf(),
    val focusedMarkerPosition: Pair<Double, Double>? = null,
    val selectedPhotoBooth: PhotoBooth? = null,
    val isShowInfoDialog: Boolean = false,
    val isShowDirectionBottomSheet: Boolean = false,
    val isShowLocationPermissionDialog: Boolean = false,
)

data class MapBounds(
    val southWest: LatLng,  // 남서 (좌하단)
    val northWest: LatLng,  // 북서 (좌상단)
    val northEast: LatLng,  // 북동 (우상단)
    val southEast: LatLng,  // 남동 (우하단)
) {
    val center: LatLng
        get() = LatLng(
            latitude = (southWest.latitude + northEast.latitude) / 2,
            longitude = (southWest.longitude + northEast.longitude) / 2,
        )
}

data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

sealed interface MapIntent {
    data object EnterMapScreen : MapIntent

    // in 지도
    data class ClickPhotoBoothMarker(
        val latitude: Double,
        val longitude: Double,
    ) : MapIntent
    data object ClickDirection : MapIntent
    data object ClickRefresh : MapIntent
    data class UpdateCurrentLocation(val latitude: Double, val longitude: Double) : MapIntent
    data class UpdateMapBounds(val mapBounds: MapBounds) : MapIntent

    // in 패널
    data object ClickCurrentLocation : MapIntent
    data object ClickInfoIcon : MapIntent
    data object ClickCloseInfoIcon : MapIntent
    data object ClickToMapChip : MapIntent
    data class ClickBrand(val brand: Brand) : MapIntent
    data class ClickNearPhotoBooth(val photoBooth: PhotoBooth) : MapIntent
    data object ClickClosePhotoBoothCard : MapIntent
    data object OpenDirectionBottomSheet : MapIntent
    data object CloseDirectionBottomSheet : MapIntent
    data class ClickDirectionItem(val app: DirectionApp) : MapIntent
    data class ChangeDragLevel(val dragLevel: DragLevel) : MapIntent

    // 위치 권한
    data object RequestLocationPermission : MapIntent
    data object ShowLocationPermissionDialog : MapIntent
    data object DismissLocationPermissionDialog : MapIntent
    data object ConfirmLocationPermissionDialog : MapIntent
}

sealed interface MapEffect {
    data object RefreshCurrentLocation : MapEffect
    data object OpenDirectionBottomSheet : MapEffect
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
