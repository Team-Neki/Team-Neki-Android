package com.neki.android.feature.map.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.map.impl.const.MapConst
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
            is MapIntent.EnterMapScreen -> handleEnterMapScreen(intent, state, reduce, postSideEffect)
            is MapIntent.ClickRefreshButton -> loadPhotoBoothsByPolygon(intent.mapBounds, state, reduce, postSideEffect)
            is MapIntent.UpdateCurrentLocation -> handleUpdateCurrentLocation(state, intent, reduce)
            MapIntent.ClickCurrentLocation -> postSideEffect(MapEffect.RefreshCurrentLocation)
            MapIntent.ClickInfoIcon -> reduce { copy(isShowInfoDialog = true) }
            MapIntent.ClickCloseInfoIcon -> reduce { copy(isShowInfoDialog = false) }
            MapIntent.ClickToMapChip -> reduce { copy(dragLevel = DragLevel.FIRST) }
            is MapIntent.ClickBrand -> handleClickBrand(state, intent, reduce)
            is MapIntent.ClickNearPhotoBooth -> handleClickNearBrand(intent, reduce, postSideEffect)
            MapIntent.ClickClosePhotoBoothCard -> reduce {
                copy(
                    dragLevel = DragLevel.SECOND,
                    mapMarkers = mapMarkers.map { it.copy(isFocused = false) }.toImmutableList(),
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

    private fun handleEnterMapScreen(
        intent: MapIntent.EnterMapScreen,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        loadBrands(reduce)

        if (!intent.hasLocationPermission) {
            // 권한이 없으면 강남역으로 카메라 이동 (카메라 이동 완료 후 MapScreen에서 polygon 조회)
            postSideEffect(
                MapEffect.MoveCameraToPosition(
                    latitude = MapConst.DEFAULT_LATITUDE,
                    longitude = MapConst.DEFAULT_LONGITUDE,
                ),
            )
        }
        // 권한이 있으면 onLocationChange -> UpdateCurrentLocation에서 현위치 기반 API 호출
    }

    private fun handleUpdateCurrentLocation(
        state: MapState,
        intent: MapIntent.UpdateCurrentLocation,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        val isFirstLocation = state.currentLocation == null
        reduce { copy(currentLocation = Location(intent.latitude, intent.longitude)) }

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

        state.currentLocation?.let { location ->
            val checkedBrandIds = updatedBrands.filter { it.isChecked }.map { it.id }
            loadNearbyPhotoBooths(
                longitude = location.longitude,
                latitude = location.latitude,
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
            val isAlreadyInMarkers = mapMarkers.any {
                it.latitude == intent.photoBooth.latitude && it.longitude == intent.photoBooth.longitude
            }
            val updatedMarkers = if (isAlreadyInMarkers) {
                mapMarkers.map { marker ->
                    marker.copy(isFocused = marker.id == intent.photoBooth.id)
                }
            } else {
                mapMarkers.map { it.copy(isFocused = false) } + intent.photoBooth.copy(isFocused = true)
            }
            copy(
                dragLevel = DragLevel.INVISIBLE,
                mapMarkers = updatedMarkers.toImmutableList(),
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
        state.mapMarkers.find { it.isFocused }?.let { focusedPhotoBooth ->
            postSideEffect(
                MapEffect.MoveDirectionApp(
                    app = intent.app,
                    startLatitude = state.currentLocation.latitude,
                    startLongitude = state.currentLocation.longitude,
                    endLatitude = focusedPhotoBooth.latitude,
                    endLongitude = focusedPhotoBooth.longitude,
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
        reduce {
            val updatedMarkers = mapMarkers.map { marker ->
                val isClicked = marker.latitude == intent.latitude && marker.longitude == intent.longitude
                if (isClicked) {
                    val distance = currentLocation?.let { location ->
                        calculateDistance(location.latitude, location.longitude, marker.latitude, marker.longitude)
                    } ?: 0
                    marker.copy(isFocused = true, distance = distance)
                } else {
                    marker.copy(isFocused = false)
                }
            }
            copy(
                dragLevel = DragLevel.INVISIBLE,
                mapMarkers = updatedMarkers.toImmutableList(),
            )
        }
        postSideEffect(MapEffect.MoveCameraToPosition(intent.latitude, intent.longitude))
    }

    private fun loadBrands(reduce: (MapState.() -> MapState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            mapRepository.getBrands()
                .onSuccess { loadedBrands ->
                    reduce {
                        copy(
                            isLoading = false,
                            brands = loadedBrands.toImmutableList(),
                        )
                    }

                    // brands 로드 완료 후, currentLocation이 있으면 nearbyPhotoBooths 조회
                    val currentState = store.uiState.value
                    currentState.currentLocation?.let { location ->
                        if (currentState.nearbyPhotoBooths.isEmpty()) {
                            val checkedBrandIds = loadedBrands.filter { it.isChecked }.map { it.id }
                            loadNearbyPhotoBooths(
                                longitude = location.longitude,
                                latitude = location.latitude,
                                brandIds = checkedBrandIds,
                                reduce = reduce,
                            )
                        }
                    }
                }
                .onFailure {
                    reduce { copy(isLoading = false) }
                }
        }
    }

    private fun loadNearbyPhotoBooths(
        longitude: Double?,
        latitude: Double?,
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
        mapBounds: MapBounds,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
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
            reduce { copy(isLoading = true) }

            mapRepository.getPhotoBoothsByPolygon(
                coordinates = coordinates,
                brandIds = checkedBrandIds,
            ).onSuccess { photoBooths ->
                reduce {
                    val photoBoothsWithImage = photoBooths.map { photoBooth ->
                        val matchedBrand = brands.find { it.name == photoBooth.brandName }
                        photoBooth.copy(imageUrl = matchedBrand?.imageUrl.orEmpty())
                    }
                    copy(
                        isLoading = false,
                        mapMarkers = photoBoothsWithImage.toImmutableList(),
                    )
                }
            }.onFailure {
                reduce { copy(isLoading = false) }
                postSideEffect(MapEffect.ShowToastMessage("포토부스 조회에 실패했습니다."))
            }
        }
    }
}
