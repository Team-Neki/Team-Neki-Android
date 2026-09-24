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
import com.neki.android.core.analytics.event.MapAnalyticsEvent
import com.neki.android.core.analytics.logger.AnalyticsLogger
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.map.impl.const.DirectionApp
import com.neki.android.feature.map.impl.const.MapConst
import com.neki.android.feature.map.impl.util.LocationHelper
import com.neki.android.feature.map.impl.util.calculateDistance
import com.neki.android.feature.map.impl.util.getSecondDepthRegionName
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Suppress("LargeClass")
@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mapRepository: MapRepository,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    private var lastSearchCenter: LocLatLng? = null
    private var polygonMarkerIds = emptySet<Long>()

    val store: MviIntentStore<MapState, MapIntent, MapEffect> = mviIntentStore(
        initialState = MapState(),
        onIntent = ::onIntent,
        initialFetchData = { store.onIntent(MapIntent.EnterMapScreen) },
    )

    fun logMapView() {
        analyticsLogger.log(MapAnalyticsEvent.MapView)
    }

    private fun onIntent(
        intent: MapIntent,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        when (intent) {
            MapIntent.EnterMapScreen -> fetchInitialData(reduce)
            is MapIntent.GrantedLocationPermission -> getCurrentLocation(reduce, postSideEffect)
            is MapIntent.LoadPhotoBoothsByBounds -> {
                loadPhotoBoothsByPolygon(intent.mapBounds, state, reduce, postSideEffect)
            }
            MapIntent.ClickCurrentLocationIcon -> {
                if (LocationPermissionManager.isGrantedLocationPermission(context)) moveCurrentLocation(state, reduce, postSideEffect)
                else postSideEffect(MapEffect.LaunchLocationPermission)
            }

            MapIntent.GestureOnMap -> reduce { copy(isCameraOnCurrentLocation = false, isVisibleRefreshButton = true) }
            is MapIntent.ClickRefreshButton -> {
                analyticsLogger.log(
                    MapAnalyticsEvent.MapReSearch(
                        hasFilter = state.brands.any { it.isChecked },
                        regionChanged = isRegionChanged(intent.center, intent.zoomLevel),
                    ),
                )
                lastSearchCenter = intent.center
                reduce { copy(isVisibleRefreshButton = false) }
                loadPhotoBoothsByPolygon(intent.mapBounds, state, reduce, postSideEffect, intent.center)
            }
            is MapIntent.UpdateCurrentLocation -> handleUpdateCurrentLocation(intent.locLatLng, reduce)
            MapIntent.ClickToMapChip -> reduce { copy(dragLevel = DragLevel.FIRST) }
            is MapIntent.ClickVerticalBrand -> handleClickBrand(intent.brand, state, reduce)
            is MapIntent.ClickPhotoBoothListItem -> handleClickPhotoBoothListItem(intent.photoBooth, reduce, postSideEffect)
            MapIntent.ClickClosePhotoBoothCard -> reduce {
                copy(
                    dragLevel = DragLevel.SECOND,
                    mapMarkers = mapMarkers
                        .filter { polygonMarkerIds.contains(it.id) }
                        .map { it.copy(isFocused = false) }
                        .toImmutableList(),
                    favoritePhotoBooths = favoritePhotoBooths
                        .map { it.copy(isFocused = false) }
                        .toImmutableList(),
                )
            }

            MapIntent.OpenDirectionBottomSheet -> reduce { copy(isShowDirectionBottomSheet = true) }
            MapIntent.CloseDirectionBottomSheet -> reduce { copy(isShowDirectionBottomSheet = false) }
            is MapIntent.ClickDirectionItem -> handleClickDirectionItem(state, intent.app, reduce, postSideEffect)
            is MapIntent.ChangeDragLevel -> handleChangeDragLevel(intent.dragLevel, reduce)
            is MapIntent.ClickPhotoBoothMarker -> handleClickPhotoBoothMarker(intent.locLatLng, state, reduce, postSideEffect)
            is MapIntent.ClickClusterMarker -> postSideEffect(MapEffect.ZoomToClusterBounds(intent.southWest, intent.northEast))
            is MapIntent.ClickPhotoBoothCard -> handleClickPhotoBoothCard(intent.locLatLng, postSideEffect)
            MapIntent.ClickDirectionIcon -> {
                postSideEffect(
                    if (LocationPermissionManager.isGrantedLocationPermission(context)) {
                        MapEffect.OpenDirectionBottomSheet
                    } else {
                        MapEffect.LaunchLocationPermission
                    },
                )
            }

            MapIntent.RequestLocationPermission -> postSideEffect(MapEffect.LaunchLocationPermission)
            MapIntent.ShowLocationPermissionDialog -> reduce { copy(isShowLocationPermissionDialog = true) }
            MapIntent.DismissLocationPermissionDialog -> reduce { copy(isShowLocationPermissionDialog = false) }
            MapIntent.ConfirmLocationPermissionDialog -> {
                reduce { copy(isShowLocationPermissionDialog = false) }
                postSideEffect(MapEffect.NavigateToAppSettings)
            }
            MapIntent.ClickEditBrandOrder -> {
                postSideEffect(MapEffect.NavigateToPhotoBoothOrderChange)
            }
            is MapIntent.UpdateBrandOrder -> reduce { copy(brands = intent.orderedBrands.toImmutableList()) }
            is MapIntent.ClickPhotoBoothFavorite -> {
                val newFavorite = !intent.photoBooth.favorite
                toggleFavorite(intent.photoBooth, newFavorite, reduce)
                updateFavorite(intent.photoBooth.copy(favorite = newFavorite))
            }
            is MapIntent.RevertFavoritePhotoBooth -> {
                toggleFavorite(intent.photoBooth, intent.photoBooth.favorite, reduce)
            }
            MapIntent.ClickShowFavoriteIcon -> {
                val newShowFavorite = !state.showFavoritePhotoBooth
                if (newShowFavorite) {
                    analyticsLogger.log(MapAnalyticsEvent.FavoriteBoothFilterOn(favoriteBoothCount = state.favoritePhotoBooths.size))
                } else {
                    analyticsLogger.log(MapAnalyticsEvent.FavoriteBoothFilterOff)
                }
                reduce {
                    copy(
                        showFavoritePhotoBooth = newShowFavorite,
                        favoritePhotoBooths = favoritePhotoBooths.map { it.copy(isFocused = false) }.toImmutableList(),
                    )
                }
            }
            is MapIntent.SelectTab -> {
                if (intent.tab == state.selectedTab) return@onIntent
                if (intent.tab == MapTab.FAVORITE) {
                    analyticsLogger.log(MapAnalyticsEvent.FavoriteBoothView(favoriteBoothCount = state.favoritePhotoBooths.size))
                }
                reduce {
                    val updatedArea = areaPhotoBooths.map { it.copy(isCheckedBrand = true) }.toImmutableList()
                    val updatedFavorite = favoritePhotoBooths.map { it.copy(isCheckedBrand = true) }.toImmutableList()
                    copy(
                        selectedTab = intent.tab,
                        brands = brands.map { it.copy(isChecked = false) }.toImmutableList(),
                        mapMarkers = mapMarkers.map { it.copy(isCheckedBrand = true) }.toImmutableList(),
                        areaPhotoBooths = updatedArea,
                        favoritePhotoBooths = updatedFavorite,
                        displayPhotoBooths = displayPhotoBooths(
                            selectedTab = intent.tab,
                            areaPhotoBooths = updatedArea,
                            favoritePhotoBooths = updatedFavorite,
                            favoritePhotoBoothSort = favoritePhotoBoothSort,
                            currentLocation = currentLocLatLng,
                        ),
                    )
                }
            }
            is MapIntent.SelectFavoritePhotoBoothSort -> {
                val hasPermission = LocationPermissionManager.isGrantedLocationPermission(context)
                if (intent.sort == FavoritePhotoBoothSort.DISTANCE) {
                    if (!hasPermission) postSideEffect(MapEffect.LaunchLocationPermission)
                    else if (state.currentLocLatLng == null) getCurrentLocation(reduce, postSideEffect)
                }
                reduce {
                    copy(
                        favoritePhotoBoothSort = intent.sort,
                        displayPhotoBooths = displayPhotoBooths(
                            selectedTab = selectedTab,
                            areaPhotoBooths = areaPhotoBooths,
                            favoritePhotoBooths = favoritePhotoBooths,
                            favoritePhotoBoothSort = intent.sort,
                            currentLocation = currentLocLatLng,
                        ),
                    )
                }
            }
            is MapIntent.ShowToast -> postSideEffect(MapEffect.ShowToastMessage(intent.message))
        }
    }

    private fun updateFavorite(photoBooth: PhotoBooth) {
        viewModelScope.launch {
            mapRepository.updatePhotoBoothFavorite(photoBooth.id, photoBooth.favorite)
                .onSuccess {
                    if (photoBooth.favorite) {
                        analyticsLogger.log(
                            MapAnalyticsEvent.BoothFavoriteAdd(
                                boothName = photoBooth.branchName,
                                brandName = photoBooth.brandName,
                            ),
                        )
                    } else {
                        analyticsLogger.log(
                            MapAnalyticsEvent.BoothFavoriteRemove(
                                boothName = photoBooth.branchName,
                                brandName = photoBooth.brandName,
                            ),
                        )
                    }
                    store.onIntent(
                        MapIntent.ShowToast(
                            message = if (photoBooth.favorite) "저장한 포토 부스에 추가됐어요!" else "저장한 포토 부스에서 삭제됐어요!",
                        ),
                    )
                }
                .onFailure { e ->
                    Timber.e(e)
                    store.onIntent(MapIntent.RevertFavoritePhotoBooth(photoBooth.copy(favorite = !photoBooth.favorite)))
                }
        }
    }

    private fun toggleFavorite(
        photoBooth: PhotoBooth,
        newFavorite: Boolean,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        val id = photoBooth.id
        val isPolygonMarker = polygonMarkerIds.contains(id)
        reduce {
            val updatedArea = areaPhotoBooths.map { if (it.id == id) it.copy(favorite = newFavorite) else it }.toImmutableList()
            val updatedFavorite = when {
                newFavorite && favoritePhotoBooths.none { it.id == id } -> {
                    val booth = photoBooth.copy(
                        favorite = true,
                        imageUrl = brands.find { it.name == photoBooth.brandName }?.imageUrl.orEmpty(),
                    )
                    (favoritePhotoBooths + booth).toImmutableList()
                }
                !newFavorite -> favoritePhotoBooths.filter { it.id != id }.toImmutableList()
                else -> favoritePhotoBooths
            }
            val isFocusedMarker = mapMarkers.any { it.id == id && it.isFocused }
            val updatedMarkers = if (!newFavorite && !isPolygonMarker && !isFocusedMarker) {
                mapMarkers.filter { it.id != id }.toImmutableList()
            } else {
                mapMarkers.map { if (it.id == id) it.copy(favorite = newFavorite) else it }.toImmutableList()
            }
            copy(
                mapMarkers = updatedMarkers,
                areaPhotoBooths = updatedArea,
                favoritePhotoBooths = updatedFavorite,
                displayPhotoBooths = displayPhotoBooths(
                    selectedTab = selectedTab,
                    areaPhotoBooths = updatedArea,
                    favoritePhotoBooths = updatedFavorite,
                    favoritePhotoBoothSort = favoritePhotoBoothSort,
                    currentLocation = currentLocLatLng,
                ),
            )
        }
    }

    private fun getCurrentLocation(
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        viewModelScope.launch {
            LocationHelper.getCurrentLocation(context)
                .onSuccess { location ->
                    if (!LocationPermissionManager.isGrantedLocationPermission(context)) return@onSuccess
                    handleUpdateCurrentLocation(location, reduce)
                    reduce { copy(isCameraOnCurrentLocation = true, isVisibleRefreshButton = false) }
                    postSideEffect(
                        MapEffect.MoveCameraToPosition(
                            locLatLng = location,
                            isRequiredLoadPhotoBooths = true,
                            zoomLevel = MapConst.MARKER_SELECTED_ZOOM_LEVEL,
                        ),
                    )
                }
                .onFailure { e ->
                    Timber.e(e)
                    // 위치 조회 실패 시 강남역으로 카메라 이동
                    postSideEffect(
                        MapEffect.MoveCameraToPosition(
                            locLatLng = LocLatLng(MapConst.DEFAULT_LATITUDE, MapConst.DEFAULT_LONGITUDE),
                            isRequiredLoadPhotoBooths = true,
                            zoomLevel = MapConst.MARKER_SELECTED_ZOOM_LEVEL,
                        ),
                    )
                }
        }
    }

    private fun handleUpdateCurrentLocation(
        locLatLng: LocLatLng,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        if (!LocationPermissionManager.isGrantedLocationPermission(context)) {
            return
        }
        reduce {
            copy(
                currentLocLatLng = locLatLng,
                displayPhotoBooths = displayPhotoBooths(
                    selectedTab = selectedTab,
                    areaPhotoBooths = areaPhotoBooths,
                    favoritePhotoBooths = favoritePhotoBooths,
                    favoritePhotoBoothSort = favoritePhotoBoothSort,
                    currentLocation = locLatLng,
                ),
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

        if (state.currentLocLatLng != null && LocationPermissionManager.isGrantedLocationPermission(context)) {
            reduce { copy(isCameraOnCurrentLocation = true, isVisibleRefreshButton = false) }
            postSideEffect(
                MapEffect.MoveCameraToPosition(
                    locLatLng = LocLatLng(state.currentLocLatLng.latitude, state.currentLocLatLng.longitude),
                    isRequiredLoadPhotoBooths = true,
                    zoomLevel = MapConst.MARKER_SELECTED_ZOOM_LEVEL,
                ),
            )
        }
    }

    private fun handleClickBrand(
        clickedBrand: Brand,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        val updatedBrands = state.brands.map { brand ->
            if (brand == clickedBrand) {
                brand.copy(isChecked = !brand.isChecked)
            } else {
                brand
            }
        }
        analyticsLogger.log(
            MapAnalyticsEvent.MapBrandFilterToggle(
                action = if (clickedBrand.isChecked) "deselect" else "select",
                selectedCount = updatedBrands.count { it.isChecked },
                brandName = clickedBrand.name,
            ),
        )
        reduce {
            val checkedBrandNames = updatedBrands.filter { it.isChecked }.map { it.name }
            val isCheckedBrand = { brandName: String ->
                checkedBrandNames.isEmpty() || brandName in checkedBrandNames
            }
            val updatedArea = areaPhotoBooths.map { it.copy(isCheckedBrand = isCheckedBrand(it.brandName)) }.toImmutableList()
            val updatedFavorite = favoritePhotoBooths.map { it.copy(isCheckedBrand = isCheckedBrand(it.brandName)) }.toImmutableList()
            copy(
                brands = updatedBrands.toImmutableList(),
                mapMarkers = mapMarkers.map { it.copy(isCheckedBrand = isCheckedBrand(it.brandName)) }.toImmutableList(),
                areaPhotoBooths = updatedArea,
                favoritePhotoBooths = updatedFavorite,
                displayPhotoBooths = displayPhotoBooths(
                    selectedTab = selectedTab,
                    areaPhotoBooths = updatedArea,
                    favoritePhotoBooths = updatedFavorite,
                    favoritePhotoBoothSort = favoritePhotoBoothSort,
                    currentLocation = currentLocLatLng,
                ),
            )
        }
    }

    private fun handleClickPhotoBoothListItem(
        photoBooth: PhotoBooth,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        analyticsLogger.log(
            MapAnalyticsEvent.BoothSelect(
                entryPoint = "bottom_sheet",
                brandName = photoBooth.brandName,
            ),
        )

        reduce {
            val distance = currentLocLatLng?.let {
                calculateDistance(it.latitude, it.longitude, photoBooth.latitude, photoBooth.longitude)
            } ?: 0
            val isAlreadyInMarkers = mapMarkers.any {
                it.latitude == photoBooth.latitude && it.longitude == photoBooth.longitude
            }
            val updatedMarkers = if (isAlreadyInMarkers) {
                mapMarkers.map { marker ->
                    marker.copy(
                        isFocused = marker.id == photoBooth.id,
                        distance = if (marker.id == photoBooth.id) distance else marker.distance,
                    )
                }
            } else {
                mapMarkers.map { it.copy(isFocused = false) } + photoBooth.copy(isFocused = true, distance = distance)
            }
            copy(
                dragLevel = DragLevel.INVISIBLE,
                mapMarkers = updatedMarkers.toImmutableList(),
                favoritePhotoBooths = favoritePhotoBooths.map { marker ->
                    marker.copy(isFocused = marker.id == photoBooth.id, distance = if (marker.id == photoBooth.id) distance else marker.distance)
                }.toImmutableList(),
            )
        }

        postSideEffect(
            MapEffect.MoveCameraToPosition(
                locLatLng = LocLatLng(photoBooth.latitude, photoBooth.longitude),
                zoomLevel = MapConst.MARKER_SELECTED_ZOOM_LEVEL,
            ),
        )
    }

    private fun handleClickDirectionItem(
        state: MapState,
        app: DirectionApp,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        analyticsLogger.log(
            MapAnalyticsEvent.MapRouteClick(
                mapType = when (app) {
                    DirectionApp.KAKAO_MAP -> "kakao_map"
                    DirectionApp.NAVER_MAP -> "naver_map"
                    DirectionApp.GOOGLE_MAP -> "google_map"
                },
            ),
        )
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
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        val clickedBooth = state.mapMarkers.find {
            it.latitude == locLatLng.latitude && it.longitude == locLatLng.longitude
        } ?: state.favoritePhotoBooths.find {
            it.latitude == locLatLng.latitude && it.longitude == locLatLng.longitude
        }
        clickedBooth?.let { booth ->
            analyticsLogger.log(
                MapAnalyticsEvent.BoothSelect(
                    entryPoint = "map",
                    brandName = booth.brandName,
                ),
            )
        }
        reduce {
            val isClicked = { marker: PhotoBooth ->
                marker.latitude == locLatLng.latitude && marker.longitude == locLatLng.longitude
            }
            val baseMarkers = if (mapMarkers.none(isClicked)) {
                val favoriteMarker = favoritePhotoBooths.find(isClicked)
                if (favoriteMarker != null) (mapMarkers + favoriteMarker).toImmutableList() else mapMarkers
            } else {
                mapMarkers
            }
            val distance = currentLocLatLng?.let {
                calculateDistance(it.latitude, it.longitude, locLatLng.latitude, locLatLng.longitude)
            } ?: 0
            copy(
                dragLevel = DragLevel.INVISIBLE,
                mapMarkers = baseMarkers.map { marker ->
                    marker.copy(isFocused = isClicked(marker), distance = if (isClicked(marker)) distance else marker.distance)
                }.toImmutableList(),
                favoritePhotoBooths = favoritePhotoBooths.map { marker ->
                    marker.copy(isFocused = isClicked(marker), distance = if (isClicked(marker)) distance else marker.distance)
                }.toImmutableList(),
            )
        }
        postSideEffect(MapEffect.MoveCameraToPosition(locLatLng = locLatLng, zoomLevel = MapConst.MARKER_SELECTED_ZOOM_LEVEL))
    }

    private fun handleClickPhotoBoothCard(
        locLatLng: LocLatLng,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        postSideEffect(MapEffect.MoveCameraToPosition(locLatLng = locLatLng, zoomLevel = MapConst.MARKER_SELECTED_ZOOM_LEVEL))
    }

    private fun fetchInitialData(reduce: (MapState.() -> MapState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            val brandsDeferred = async { mapRepository.getBrands() }
            val favoritesDeferred = async { mapRepository.getFavoritePhotoBooths() }

            val brandsResult = brandsDeferred.await()
            val favoritesResult = favoritesDeferred.await()

            brandsResult
                .onSuccess { loadedBrands ->
                    reduce {
                        val withBrandImage: (PhotoBooth) -> PhotoBooth = { booth ->
                            booth.copy(imageUrl = loadedBrands.find { it.name == booth.brandName }?.imageUrl.orEmpty())
                        }
                        copy(
                            brands = loadedBrands.toImmutableList(),
                            areaPhotoBooths = areaPhotoBooths.map(withBrandImage).toImmutableList(),
                            mapMarkers = mapMarkers.map(withBrandImage).toImmutableList(),
                            displayPhotoBooths = displayPhotoBooths.map(withBrandImage).toImmutableList(),
                        )
                    }
                    cacheBrandImages(loadedBrands, reduce)

                    favoritesResult.onSuccess { favoriteBooths ->
                        val mappedBooths = favoriteBooths.map { booth ->
                            booth.copy(
                                favorite = true,
                                imageUrl = loadedBrands.find { it.name == booth.brandName }?.imageUrl.orEmpty(),
                            )
                        }
                        reduce {
                            val updatedFavorite = mappedBooths.toImmutableList()
                            val favoriteIds = updatedFavorite.map { it.id }.toSet()
                            val updatedArea = areaPhotoBooths.map { it.copy(favorite = it.id in favoriteIds) }.toImmutableList()
                            copy(
                                areaPhotoBooths = updatedArea,
                                mapMarkers = mapMarkers.map { it.copy(favorite = it.id in favoriteIds) }.toImmutableList(),
                                favoritePhotoBooths = updatedFavorite,
                                displayPhotoBooths = displayPhotoBooths(
                                    selectedTab = selectedTab,
                                    areaPhotoBooths = updatedArea,
                                    favoritePhotoBooths = updatedFavorite,
                                    favoritePhotoBoothSort = favoritePhotoBoothSort,
                                    currentLocation = currentLocLatLng,
                                ),
                            )
                        }
                    }.onFailure { Timber.e(it) }
                }
                .onFailure { e ->
                    Timber.e(e)
                }

            reduce { copy(isLoading = false) }
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

    private fun isRegionChanged(currentCenter: LocLatLng, zoomLevel: Double): Boolean {
        val prev = lastSearchCenter ?: return false
        val distance = calculateDistance(prev.latitude, prev.longitude, currentCenter.latitude, currentCenter.longitude)
        val threshold = when {
            zoomLevel >= 18 -> 300
            zoomLevel >= 16 -> 500
            zoomLevel >= 14 -> 700
            else -> 1000
        }
        return distance >= threshold
    }

    private fun loadPhotoBoothsByPolygon(
        mapBounds: MapBounds,
        state: MapState,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
        searchCenter: LocLatLng? = null,
    ) {
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
                brandIds = emptyList(),
            ).onSuccess { photoBooths ->
                polygonMarkerIds = photoBooths.map { it.id }.toSet()
                reduce {
                    val checkedBrandNames = brands.filter { it.isChecked }.map { it.name }
                    val updatedArea = photoBooths.map { photoBooth ->
                        photoBooth.copy(
                            imageUrl = brands.find { it.name == photoBooth.brandName }?.imageUrl.orEmpty(),
                            isCheckedBrand = checkedBrandNames.isEmpty() || photoBooth.brandName in checkedBrandNames,
                            favorite = favoritePhotoBooths.any { it.id == photoBooth.id },
                        )
                    }.toImmutableList()
                    copy(
                        isLoading = false,
                        mapMarkers = updatedArea,
                        areaPhotoBooths = updatedArea,
                        displayPhotoBooths = displayPhotoBooths(
                            selectedTab = selectedTab,
                            areaPhotoBooths = updatedArea,
                            favoritePhotoBooths = favoritePhotoBooths,
                            favoritePhotoBoothSort = favoritePhotoBoothSort,
                            currentLocation = currentLocLatLng,
                        ),
                    )
                }
                if (searchCenter != null || state.areaRegionName != null || lastSearchCenter != null) {
                    val center = searchCenter ?: LocLatLng(
                        latitude = (mapBounds.northEast.latitude + mapBounds.southWest.latitude) / 2,
                        longitude = (mapBounds.northEast.longitude + mapBounds.southWest.longitude) / 2,
                    )
                    viewModelScope.launch {
                        context.getSecondDepthRegionName(center.latitude, center.longitude)
                            .onSuccess { regionName -> reduce { copy(areaRegionName = regionName) } }
                            .onFailure { Timber.w(it, "지도 지역명 조회 실패") }
                    }
                }
            }.onFailure { e ->
                Timber.e(e)
                reduce { copy(isLoading = false) }
                postSideEffect(MapEffect.ShowToastMessage("포토부스 조회에 실패했습니다."))
            }
        }
    }

    private fun displayPhotoBooths(
        selectedTab: MapTab,
        areaPhotoBooths: ImmutableList<PhotoBooth>,
        favoritePhotoBooths: ImmutableList<PhotoBooth>,
        favoritePhotoBoothSort: FavoritePhotoBoothSort,
        currentLocation: LocLatLng?,
    ): ImmutableList<PhotoBooth> {
        val filteredPhotoBooths = when (selectedTab) {
            MapTab.AREA -> areaPhotoBooths
            MapTab.FAVORITE -> favoritePhotoBooths
        }.filter { it.isCheckedBrand }

        if (
            currentLocation == null ||
            !LocationPermissionManager.isGrantedLocationPermission(context) ||
            (selectedTab == MapTab.FAVORITE && favoritePhotoBoothSort == FavoritePhotoBoothSort.SAVED)
        ) {
            return filteredPhotoBooths.toImmutableList()
        }

        val boothsWithDistance = filteredPhotoBooths.map { photoBooth ->
            photoBooth.copy(
                distance = calculateDistance(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    photoBooth.latitude,
                    photoBooth.longitude,
                ),
            )
        }
        return if (selectedTab == MapTab.FAVORITE) boothsWithDistance.sortedBy { it.distance }.toImmutableList()
        else boothsWithDistance.toImmutableList()
    }

    private fun handleChangeDragLevel(
        dragLevel: DragLevel,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        reduce { copy(dragLevel = dragLevel) }
    }
}
