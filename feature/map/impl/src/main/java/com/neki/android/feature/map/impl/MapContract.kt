package com.neki.android.feature.map.impl

import androidx.compose.ui.graphics.ImageBitmap
import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.const.DirectionApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

data class MapState(
    val isLoading: Boolean = false,
    val currentLocLatLng: LocLatLng? = null,
    val isCameraOnCurrentLocation: Boolean = false,
    val isVisibleRefreshButton: Boolean = false,
    val dragLevel: DragLevel = DragLevel.FIRST,
    val brands: ImmutableList<Brand> = persistentListOf(),
    val brandImageCache: ImmutableMap<String, ImageBitmap> = persistentMapOf(),
    val mapMarkers: ImmutableList<PhotoBooth> = persistentListOf(),
    val nearbyPhotoBooths: ImmutableList<PhotoBooth> = persistentListOf(),
    val isShowInfoDialog: Boolean = false,
    val isShowDirectionBottomSheet: Boolean = false,
    val isShowLocationPermissionDialog: Boolean = false,
)

sealed interface MapIntent {
    data object EnterMapScreen : MapIntent

    // in 지도
    data class UpdateCurrentLocation(val locLatLng: LocLatLng) : MapIntent
    data object GrantedLocationPermission : MapIntent
    data class LoadPhotoBoothsByBounds(val mapBounds: MapBounds) : MapIntent
    data class ClickPhotoBoothMarker(val locLatLng: LocLatLng) : MapIntent
    data class ClickClusterMarker(val southWest: LocLatLng, val northEast: LocLatLng) : MapIntent
    data class ClickRefreshButton(val mapBounds: MapBounds) : MapIntent
    data object ClickDirectionIcon : MapIntent
    data object GestureOnMap : MapIntent

    // in 패널
    data object ClickCurrentLocationIcon : MapIntent
    data object ClickInfoIcon : MapIntent
    data object ClickCloseInfoIcon : MapIntent
    data object ClickToMapChip : MapIntent
    data class ClickVerticalBrand(val brand: Brand) : MapIntent
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
    data class MoveCameraToPosition(
        val locLatLng: LocLatLng,
        val isRequiredLoadPhotoBooths: Boolean = false,
    ) : MapEffect
    data class ZoomToClusterBounds(
        val southWest: LocLatLng,
        val northEast: LocLatLng,
    ) : MapEffect
    data object OpenDirectionBottomSheet : MapEffect
    data class ShowToastMessage(val message: String) : MapEffect

    data class LaunchDirectionApp(
        val app: DirectionApp,
        val startLocLatLng: LocLatLng,
        val endLocLatLng: LocLatLng,
    ) : MapEffect

    data object NavigateToAppSettings : MapEffect
    data object LaunchLocationPermission : MapEffect
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
