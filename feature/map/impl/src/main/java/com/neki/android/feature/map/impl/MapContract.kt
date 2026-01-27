package com.neki.android.feature.map.impl

import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.const.DirectionApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MapState(
    val isLoading: Boolean = false,
    val currentLocLatLng: LocLatLng? = null,
    val dragLevel: DragLevel = DragLevel.FIRST,
    val brands: ImmutableList<Brand> = persistentListOf(),
    val mapMarkers: ImmutableList<PhotoBooth> = persistentListOf(),
    val nearbyPhotoBooths: ImmutableList<PhotoBooth> = persistentListOf(),
    val isShowInfoDialog: Boolean = false,
    val isShowDirectionBottomSheet: Boolean = false,
    val isShowLocationPermissionDialog: Boolean = false,
)

sealed interface MapIntent {
    data object EnterMapScreen : MapIntent

    // in 지도
    data class LoadPhotoBoothsByBounds(val mapBounds: MapBounds) : MapIntent
    data class ClickPhotoBoothMarker(val locLatLng: LocLatLng) : MapIntent
    data class ClickRefreshButton(val mapBounds: MapBounds) : MapIntent
    data object ClickDirectionIcon : MapIntent
    data class UpdateCurrentLocation(val locLatLng: LocLatLng) : MapIntent

    // in 패널
    data object ClickCurrentLocation : MapIntent
    data object ClickInfoIcon : MapIntent
    data object ClickCloseInfoIcon : MapIntent
    data object ClickToMapChip : MapIntent
    data class ClickBrand(val brand: Brand) : MapIntent
    data class ClickNearPhotoBooth(val photoBooth: PhotoBooth) : MapIntent
    data class ClickPhotoBoothCard(val locLatLng: LocLatLng) : MapIntent
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
    data object TrackingFollowMode : MapEffect
    data class MoveCameraToPosition(val locLatLng: LocLatLng) : MapEffect
    data object OpenDirectionBottomSheet : MapEffect
    data class ShowToastMessage(val message: String) : MapEffect

    data class LaunchDirectionApp(
        val app: DirectionApp,
        val startLocLatLng: LocLatLng,
        val endLocLatLng: LocLatLng,
    ) : MapEffect

    data object NavigateToAppSettings : MapEffect
    data object RequestLocationPermission : MapEffect
}

enum class DragLevel { FIRST, SECOND, THIRD, INVISIBLE }

data class MapBounds(
    val southWest: LocLatLng,
    val northWest: LocLatLng,
    val northEast: LocLatLng,
    val southEast: LocLatLng,
)

data class LocLatLng(
    val latitude: Double,
    val longitude: Double,
)
