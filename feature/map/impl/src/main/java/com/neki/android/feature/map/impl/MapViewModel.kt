package com.neki.android.feature.map.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.map.impl.util.calculateDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
) : ViewModel() {
    val store: MviIntentStore<MapState, MapIntent, MapEffect> = mviIntentStore(
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
            MapIntent.EnterMapScreen -> loadBrands(reduce)
            MapIntent.ClickRefresh -> loadPhotoBoothsByPolygon(state, reduce, postSideEffect)
            is MapIntent.UpdateCurrentLocation -> handleUpdateCurrentLocation(state, intent, reduce)
            is MapIntent.UpdateMapBounds -> reduce { copy(mapBounds = intent.mapBounds) }
            MapIntent.ClickCurrentLocation -> postSideEffect(MapEffect.RefreshCurrentLocation)
            MapIntent.ClickInfoIcon -> reduce { copy(isShowInfoDialog = true) }
            MapIntent.ClickCloseInfoIcon -> reduce { copy(isShowInfoDialog = false) }
            MapIntent.ClickToMapChip -> reduce { copy(dragLevel = DragLevel.FIRST) }
            is MapIntent.ClickBrand -> handleClickBrand(state, intent, reduce)
            is MapIntent.ClickNearPhotoBooth -> handleClickNearBrand(intent, reduce, postSideEffect)
            MapIntent.ClickClosePhotoBoothCard -> reduce {
                copy(
                    dragLevel = DragLevel.SECOND,
                    focusedMarkerPosition = null,
                    selectedPhotoBooth = null,
                )
            }
            MapIntent.OpenDirectionBottomSheet -> reduce { copy(isShowDirectionBottomSheet = true) }
            MapIntent.CloseDirectionBottomSheet -> reduce { copy(isShowDirectionBottomSheet = false) }
            is MapIntent.ClickDirectionItem -> handleClickDirectionItem(state, intent, reduce, postSideEffect)
            is MapIntent.ChangeDragLevel -> reduce { copy(dragLevel = intent.dragLevel) }
            is MapIntent.ClickPhotoBoothMarker -> handleClickBrandMarker(state, intent, reduce, postSideEffect)
            MapIntent.ClickDirection -> postSideEffect(MapEffect.OpenDirectionBottomSheet)
            MapIntent.RequestLocationPermission -> postSideEffect(MapEffect.RequestLocationPermission)
            MapIntent.ShowLocationPermissionDialog -> reduce { copy(isShowLocationPermissionDialog = true) }
            MapIntent.DismissLocationPermissionDialog -> reduce { copy(isShowLocationPermissionDialog = false) }
            MapIntent.ConfirmLocationPermissionDialog -> {
                reduce { copy(isShowLocationPermissionDialog = false) }
                postSideEffect(MapEffect.NavigateToAppSettings)
            }
        }
    }

    private fun handleUpdateCurrentLocation(
        state: MapState,
        intent: MapIntent.UpdateCurrentLocation,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        val isFirstLocation = state.currentLocation == null
        reduce { copy(currentLocation = intent.latitude to intent.longitude) }

        if (isFirstLocation) {
            val checkedBrandIds = state.brands.filter { it.isChecked }.map { it.id }
            loadNearbyPhotoBooths(
                longitude = intent.longitude,
                latitude = intent.latitude,
                brandIds = checkedBrandIds,
                reduce = reduce,
            )
        }
    }

    private fun handleClickBrand(
        state: MapState,
        intent: MapIntent.ClickBrand,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        val updatedBrands = state.brands.map { brand ->
            if (brand == intent.brand) {
                brand.copy(isChecked = !brand.isChecked)
            } else {
                brand
            }
        }
        reduce { copy(brands = updatedBrands.toImmutableList()) }

        state.currentLocation?.let { (latitude, longitude) ->
            val checkedBrandIds = updatedBrands.filter { it.isChecked }.map { it.id }
            loadNearbyPhotoBooths(
                longitude = longitude,
                latitude = latitude,
                brandIds = checkedBrandIds,
                reduce = reduce,
            )
        }
    }

    private fun handleClickNearBrand(
        intent: MapIntent.ClickNearPhotoBooth,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce {
            copy(
                dragLevel = DragLevel.INVISIBLE,
                selectedPhotoBooth = intent.photoBooth,
                focusedMarkerPosition = intent.photoBooth.latitude to intent.photoBooth.longitude,
            )
        }
        postSideEffect(MapEffect.MoveCameraToPosition(intent.photoBooth.latitude, intent.photoBooth.longitude))
    }

    private fun handleClickDirectionItem(
        state: MapState,
        intent: MapIntent.ClickDirectionItem,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce { copy(isShowDirectionBottomSheet = false) }
        if (state.currentLocation == null) {
            postSideEffect(MapEffect.ShowToastMessage("현재 위치를 가져올 수 없습니다."))
            return
        }
        state.selectedPhotoBooth?.let { brandInfo ->
            postSideEffect(
                MapEffect.MoveDirectionApp(
                    app = intent.app,
                    startLatitude = state.currentLocation.first,
                    startLongitude = state.currentLocation.second,
                    endLatitude = brandInfo.latitude,
                    endLongitude = brandInfo.longitude,
                ),
            )
        }
    }

    private fun handleClickBrandMarker(
        state: MapState,
        intent: MapIntent.ClickPhotoBoothMarker,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        val selectedBrand = state.mapMarkers.find { it.latitude == intent.latitude && it.longitude == intent.longitude }
        val selectedBrandWithDistance = selectedBrand?.let { brand ->
            state.currentLocation?.let { (currentLat, currentLng) ->
                val distance = calculateDistance(currentLat, currentLng, brand.latitude, brand.longitude)
                brand.copy(distance = distance)
            } ?: brand
        }
        reduce {
            copy(
                dragLevel = DragLevel.INVISIBLE,
                focusedMarkerPosition = intent.latitude to intent.longitude,
                selectedPhotoBooth = selectedBrandWithDistance,
            )
        }
        postSideEffect(MapEffect.MoveCameraToPosition(intent.latitude, intent.longitude))
    }

    private fun loadBrands(reduce: (MapState.() -> MapState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            mapRepository.getBrands()
                .onSuccess { brands ->
                    reduce {
                        copy(
                            isLoading = false,
                            brands = brands.toImmutableList(),
                        )
                    }
                }
                .onFailure {
                    reduce { copy(isLoading = false) }
                }
        }
    }

    private fun loadNearbyPhotoBooths(
        longitude: Double,
        latitude: Double,
        radiusInMeters: Int = 1000,
        brandIds: List<Long> = emptyList(),
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        viewModelScope.launch {
            mapRepository.getPhotoBoothsByPoint(
                longitude = longitude,
                latitude = latitude,
                radiusInMeters = radiusInMeters,
                brandIds = brandIds,
            ).onSuccess { photoBooths ->
                reduce {
                    val photoBoothsWithImage = photoBooths.map { photoBooth ->
                        val matchedBrand = brands.find { it.name == photoBooth.brandName }
                        photoBooth.copy(imageUrl = matchedBrand?.imageUrl.orEmpty())
                    }
                    copy(nearbyPhotoBooths = photoBoothsWithImage.toImmutableList())
                }
            }
        }
    }

    private fun loadPhotoBoothsByPolygon(
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        val mapBounds = state.mapBounds
        if (mapBounds == null) {
            postSideEffect(MapEffect.ShowToastMessage("지도 영역을 가져올 수 없습니다."))
            return
        }

        val checkedBrandIds = state.brands.filter { it.isChecked }.map { it.id }

        // 좌상단 -> 우상단 -> 우하단 -> 좌하단 -> 좌상단 (닫힌 다각형)
        val coordinates = listOf(
            mapBounds.northWest.longitude to mapBounds.northWest.latitude,
            mapBounds.northEast.longitude to mapBounds.northEast.latitude,
            mapBounds.southEast.longitude to mapBounds.southEast.latitude,
            mapBounds.southWest.longitude to mapBounds.southWest.latitude,
            mapBounds.northWest.longitude to mapBounds.northWest.latitude,
        )

        viewModelScope.launch {
            mapRepository.getPhotoBoothsByPolygon(
                coordinates = coordinates,
                brandIds = checkedBrandIds,
            ).onSuccess { photoBooths ->
                reduce {
                    val photoBoothsWithImage = photoBooths.map { photoBooth ->
                        val matchedBrand = brands.find { it.name == photoBooth.brandName }
                        photoBooth.copy(imageUrl = matchedBrand?.imageUrl.orEmpty())
                    }
                    copy(mapMarkers = photoBoothsWithImage.toImmutableList())
                }
            }.onFailure {
                postSideEffect(MapEffect.ShowToastMessage("포토부스 조회에 실패했습니다."))
            }
        }
    }
}
