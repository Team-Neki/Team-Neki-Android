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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
) : ViewModel() {
    val store: MviIntentStore<MapState, MapIntent, MapEffect> = mviIntentStore(
        initialState = MapState(),
        onIntent = ::onIntent,
        initialFetchData = { store.onIntent(MapIntent.EnterMapScreen) },
    )

    private fun onIntent(
        intent: MapIntent,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        when (intent) {
            MapIntent.EnterMapScreen -> loadBrands(state, reduce)
            is MapIntent.LoadPhotoBoothsByBounds -> loadPhotoBoothsByPolygon(intent.mapBounds, state, reduce, postSideEffect)
            is MapIntent.ClickRefreshButton -> loadPhotoBoothsByPolygon(intent.mapBounds, state, reduce, postSideEffect)
            is MapIntent.UpdateCurrentLocation -> handleUpdateCurrentLocation(state, intent, reduce, postSideEffect)
            MapIntent.ClickCurrentLocationIcon -> {
                if (state.dragLevel == DragLevel.INVISIBLE) {
                    reduce {
                        copy(
                            dragLevel = DragLevel.FIRST,
                            mapMarkers = mapMarkers.map { it.copy(isFocused = false) }.toImmutableList(),
                        )
                    }
                }
                postSideEffect(MapEffect.TrackingFollowMode)
            }

            MapIntent.ClickInfoIcon -> reduce { copy(isShowInfoDialog = true) }
            MapIntent.ClickCloseInfoIcon -> reduce { copy(isShowInfoDialog = false) }
            MapIntent.ClickToMapChip -> reduce { copy(dragLevel = DragLevel.FIRST) }
            is MapIntent.ClickVerticalBrand -> handleClickBrand(intent, reduce)
            is MapIntent.ClickNearPhotoBooth -> handleClickNearPhotoBooth(intent, reduce, postSideEffect)
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
            is MapIntent.ClickPhotoBoothMarker -> handleClickPhotoBoothMarker(intent, reduce, postSideEffect)
            is MapIntent.ClickPhotoBoothCard -> handleClickPhotoBoothCard(intent, postSideEffect)
            MapIntent.ClickDirectionIcon -> postSideEffect(MapEffect.OpenDirectionBottomSheet)
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
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce { copy(currentLocLatLng = intent.locLatLng) }

        /** 위치가 이동하더라도 주변 네컷 사진 브랜드는 변경하지 않기 때문에 최초에만 요청 **/
        if (state.currentLocLatLng == null) {
            loadNearbyPhotoBooths(
                longitude = intent.locLatLng.longitude,
                latitude = intent.locLatLng.latitude,
                brandIds = state.brands.filter { it.isChecked }.map { it.id },
                reduce = reduce,
            )
        }
    }

    private fun handleClickBrand(
        intent: MapIntent.ClickVerticalBrand,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        reduce {
            val updatedBrands = brands.map { brand ->
                if (brand == intent.brand) {
                    brand.copy(isChecked = !brand.isChecked)
                } else {
                    brand
                }
            }
            val checkedBrandNames = updatedBrands.filter { it.isChecked }.map { it.name }

            copy(
                brands = updatedBrands.toImmutableList(),
                mapMarkers = mapMarkers.map { photoBooth ->
                    photoBooth.copy(
                        isCheckedBrand = checkedBrandNames.isEmpty() || photoBooth.brandName in checkedBrandNames,
                    )
                }.toImmutableList(),
                nearbyPhotoBooths = nearbyPhotoBooths.map { photoBooth ->
                    photoBooth.copy(
                        isCheckedBrand = checkedBrandNames.isEmpty() || photoBooth.brandName in checkedBrandNames,
                    )
                }.toImmutableList(),
            )
        }
    }

    private fun handleClickNearPhotoBooth(
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
        postSideEffect(MapEffect.MoveCameraToPosition(LocLatLng(intent.photoBooth.latitude, intent.photoBooth.longitude)))
    }

    private fun handleClickDirectionItem(
        state: MapState,
        intent: MapIntent.ClickDirectionItem,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce { copy(isShowDirectionBottomSheet = false) }
        if (state.currentLocLatLng == null) {
            postSideEffect(MapEffect.ShowToastMessage("현재 위치를 가져올 수 없습니다."))
            return
        }
        state.mapMarkers.find { it.isFocused }?.let { focusedPhotoBooth ->
            postSideEffect(
                MapEffect.LaunchDirectionApp(
                    app = intent.app,
                    startLocLatLng = state.currentLocLatLng,
                    endLocLatLng = LocLatLng(focusedPhotoBooth.latitude, focusedPhotoBooth.longitude),
                ),
            )
        }
    }

    private fun handleClickPhotoBoothMarker(
        intent: MapIntent.ClickPhotoBoothMarker,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        Timber.d("handleClickPhotoBoothMarker START: ${intent.locLatLng}")
        reduce {
            val updatedMarkers = mapMarkers.map { marker ->
                val isClicked = marker.latitude == intent.locLatLng.latitude && marker.longitude == intent.locLatLng.longitude
                if (isClicked) {
                    val distance = currentLocLatLng?.let { location ->
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
        Timber.d("handleClickPhotoBoothMarker postSideEffect: ${intent.locLatLng}")
        postSideEffect(MapEffect.MoveCameraToPosition(intent.locLatLng))
    }

    private fun handleClickPhotoBoothCard(
        intent: MapIntent.ClickPhotoBoothCard,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        postSideEffect(MapEffect.MoveCameraToPosition(intent.locLatLng))
    }

    private fun loadBrands(
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            mapRepository.getBrands()
                .onSuccess { loadedBrands ->
                    reduce { copy(isLoading = false, brands = loadedBrands.toImmutableList()) }

                    state.currentLocLatLng?.let { location ->
                        loadNearbyPhotoBooths(
                            longitude = location.longitude,
                            latitude = location.latitude,
                            brandIds = loadedBrands.filter { it.isChecked }.map { it.id },
                            reduce = reduce,
                        )
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
                    copy(
                        nearbyPhotoBooths = photoBooths.map { photoBooth ->
                            photoBooth.copy(
                                imageUrl = brands.find {
                                    it.name == photoBooth.brandName
                                }?.imageUrl.orEmpty(),
                            )
                        }.toImmutableList(),
                    )
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
                    copy(
                        isLoading = false,
                        mapMarkers = photoBooths.map { photoBooth ->
                            photoBooth.copy(
                                imageUrl = brands.find {
                                    it.name == photoBooth.brandName
                                }?.imageUrl.orEmpty(),
                            )
                        }.toImmutableList(),
                    )
                }
            }.onFailure {
                reduce { copy(isLoading = false) }
                postSideEffect(MapEffect.ShowToastMessage("포토부스 조회에 실패했습니다."))
            }
        }
    }
}
