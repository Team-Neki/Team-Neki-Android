package com.neki.android.feature.map.impl

import android.content.Context
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.map.impl.const.DirectionApp
import com.neki.android.feature.map.impl.const.MapConst
import com.neki.android.feature.map.impl.util.LocationHelper
import com.neki.android.feature.map.impl.util.calculateDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mapRepository: MapRepository,
    private val userRepository: UserRepository,
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
            MapIntent.EnterMapScreen -> fetchInitialData(reduce)
            is MapIntent.GrantedLocationPermission -> getCurrentLocation(reduce, postSideEffect)
            is MapIntent.LoadPhotoBoothsByBounds -> loadPhotoBoothsByPolygon(intent.mapBounds, state, reduce, postSideEffect)
            MapIntent.ClickCurrentLocationIcon -> {
                if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                    moveCurrentLocation(state, reduce, postSideEffect)
                } else {
                    postSideEffect(MapEffect.LaunchLocationPermission)
                }
            }

            MapIntent.GestureOnMap -> reduce { copy(isCameraOnCurrentLocation = false, isVisibleRefreshButton = true) }
            is MapIntent.ClickRefreshButton -> {
                reduce { copy(isVisibleRefreshButton = false) }
                loadPhotoBoothsByPolygon(intent.mapBounds, state, reduce, postSideEffect)
            }
            is MapIntent.UpdateCurrentLocation -> handleUpdateCurrentLocation(state, intent.locLatLng, reduce)
            MapIntent.ClickInfoIcon -> reduce { copy(isShowInfoTooltip = true) }
            MapIntent.DismissInfoTooltip -> reduce { copy(isShowInfoTooltip = false) }
            MapIntent.ClickToMapChip -> reduce { copy(dragLevel = DragLevel.FIRST) }
            is MapIntent.ClickVerticalBrand -> handleClickBrand(intent.brand, reduce)
            is MapIntent.ClickNearPhotoBooth -> handleClickNearPhotoBooth(intent.photoBooth, reduce, postSideEffect)
            MapIntent.ClickClosePhotoBoothCard -> reduce {
                copy(
                    dragLevel = DragLevel.SECOND,
                    mapMarkers = mapMarkers.map { it.copy(isFocused = false) }.toImmutableList(),
                )
            }

            MapIntent.OpenDirectionBottomSheet -> reduce { copy(isShowDirectionBottomSheet = true) }
            MapIntent.CloseDirectionBottomSheet -> reduce { copy(isShowDirectionBottomSheet = false) }
            is MapIntent.ClickDirectionItem -> handleClickDirectionItem(state, intent.app, reduce, postSideEffect)
            is MapIntent.ChangeDragLevel -> handleChangeDragLevel(intent.dragLevel, state, reduce)
            is MapIntent.ClickPhotoBoothMarker -> handleClickPhotoBoothMarker(intent.locLatLng, reduce, postSideEffect)
            is MapIntent.ClickPhotoBoothCard -> handleClickPhotoBoothCard(intent.locLatLng, postSideEffect)
            MapIntent.ClickDirectionIcon -> {
                if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                    postSideEffect(MapEffect.OpenDirectionBottomSheet)
                } else {
                    postSideEffect(MapEffect.LaunchLocationPermission)
                }
            }

            MapIntent.RequestLocationPermission -> postSideEffect(MapEffect.LaunchLocationPermission)
            MapIntent.ShowLocationPermissionDialog -> reduce { copy(isShowLocationPermissionDialog = true) }
            MapIntent.DismissLocationPermissionDialog -> reduce { copy(isShowLocationPermissionDialog = false) }
            MapIntent.ConfirmLocationPermissionDialog -> {
                reduce { copy(isShowLocationPermissionDialog = false) }
                postSideEffect(MapEffect.NavigateToAppSettings)
            }
        }
    }

    private fun getCurrentLocation(
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        viewModelScope.launch {
            LocationHelper.getCurrentLocation(context)
                .onSuccess { location ->
                    reduce { copy(isCameraOnCurrentLocation = true, isVisibleRefreshButton = false) }
                    postSideEffect(MapEffect.MoveCameraToPosition(location, isRequiredLoadPhotoBooths = true))
                }
                .onFailure {
                    // 위치 조회 실패 시 강남역으로 카메라 이동
                    postSideEffect(
                        MapEffect.MoveCameraToPosition(
                            LocLatLng(MapConst.DEFAULT_LATITUDE, MapConst.DEFAULT_LONGITUDE),
                            isRequiredLoadPhotoBooths = true,
                        ),
                    )
                }
        }
    }

    private fun handleUpdateCurrentLocation(
        state: MapState,
        locLatLng: LocLatLng,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        reduce { copy(currentLocLatLng = locLatLng) }

        /** 위치가 이동하더라도 주변 네컷 사진 브랜드는 변경하지 않기 때문에 최초에만 요청 **/
        if (state.currentLocLatLng == null) {
            loadNearbyPhotoBooths(
                longitude = locLatLng.longitude,
                latitude = locLatLng.latitude,
                brandIds = state.brands.filter { it.isChecked }.map { it.id },
                reduce = reduce,
            )
        }
    }

    private fun moveCurrentLocation(
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        if (state.dragLevel == DragLevel.INVISIBLE) {
            reduce {
                copy(
                    dragLevel = DragLevel.FIRST,
                    mapMarkers = mapMarkers.map { it.copy(isFocused = false) }.toImmutableList(),
                )
            }
        }

        if (state.currentLocLatLng != null) {
            reduce { copy(isCameraOnCurrentLocation = true, isVisibleRefreshButton = false) }
            postSideEffect(
                MapEffect.MoveCameraToPosition(
                    LocLatLng(state.currentLocLatLng.latitude, state.currentLocLatLng.longitude),
                    isRequiredLoadPhotoBooths = true,
                ),
            )
        }
    }

    private fun handleClickBrand(
        clickedBrand: Brand,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        reduce {
            val updatedBrands = brands.map { brand ->
                if (brand == clickedBrand) {
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
        photoBooth: PhotoBooth,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce {
            val isAlreadyInMarkers = mapMarkers.any {
                it.latitude == photoBooth.latitude && it.longitude == photoBooth.longitude
            }
            val updatedMarkers = if (isAlreadyInMarkers) {
                mapMarkers.map { marker ->
                    marker.copy(isFocused = marker.id == photoBooth.id)
                }
            } else {
                mapMarkers.map { it.copy(isFocused = false) } + photoBooth.copy(isFocused = true)
            }
            copy(
                dragLevel = DragLevel.INVISIBLE,
                mapMarkers = updatedMarkers.toImmutableList(),
            )
        }
        postSideEffect(MapEffect.MoveCameraToPosition(LocLatLng(photoBooth.latitude, photoBooth.longitude)))
    }

    private fun handleClickDirectionItem(
        state: MapState,
        app: DirectionApp,
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
                    app = app,
                    startLocLatLng = state.currentLocLatLng,
                    endLocLatLng = LocLatLng(focusedPhotoBooth.latitude, focusedPhotoBooth.longitude),
                ),
            )
        }
    }

    private fun handleClickPhotoBoothMarker(
        locLatLng: LocLatLng,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce {
            val updatedMarkers = mapMarkers.map { marker ->
                val isClicked = marker.latitude == locLatLng.latitude && marker.longitude == locLatLng.longitude
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
        postSideEffect(MapEffect.MoveCameraToPosition(locLatLng))
    }

    private fun handleClickPhotoBoothCard(
        locLatLng: LocLatLng,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        postSideEffect(MapEffect.MoveCameraToPosition(locLatLng))
    }

    private fun fetchInitialData(reduce: (MapState.() -> MapState) -> Unit) {
        viewModelScope.launch {
            val hasShownInfoTooltip = userRepository.hasShownInfoToolTip.first()
            reduce { copy(isLoading = true, isShowInfoTooltip = !hasShownInfoTooltip) }

            mapRepository.getBrands()
                .onSuccess { loadedBrands ->
                    reduce { copy(isLoading = false, brands = loadedBrands.toImmutableList()) }
                    cacheBrandImages(loadedBrands, reduce)
                }
                .onFailure {
                    reduce { copy(isLoading = false) }
                }
        }
    }

    private fun cacheBrandImages(
        brands: List<Brand>,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        viewModelScope.launch {
            val imageLoader = ImageLoader(context)

            val cache = brands
                .filter { it.imageUrl.isNotEmpty() }
                .map { brand ->
                    async(Dispatchers.IO) {
                        val request = ImageRequest.Builder(context)
                            .data(brand.imageUrl)
                            .allowHardware(false)
                            .build()
                        val result = imageLoader.execute(request)
                        if (result is SuccessResult) {
                            brand.imageUrl to result.image.toBitmap().asImageBitmap()
                        } else {
                            null
                        }
                    }
                }
                .awaitAll()
                .filterNotNull()
                .toMap()

            reduce { copy(brandImageCache = cache.toImmutableMap()) }
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

    private fun handleChangeDragLevel(
        dragLevel: DragLevel,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        viewModelScope.launch {
            if (dragLevel == DragLevel.THIRD) {
                if (state.isShowInfoTooltip) {
                    userRepository.setInfoToolTipShown()
                    reduce { copy(dragLevel = dragLevel, isShowInfoTooltip = true) }
                } else {
                    reduce { copy(dragLevel = dragLevel) }
                }
            } else {
                reduce { copy(dragLevel = dragLevel) }
            }
        }
    }
}
