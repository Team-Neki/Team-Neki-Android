package com.neki.android.feature.map.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.datastore.DataStoreKey
import com.neki.android.core.dataapi.repository.DataStoreRepository
import com.neki.android.core.designsystem.R
import com.neki.android.core.model.Brand
import com.neki.android.core.model.BrandInfo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
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
            MapIntent.ClickRefresh -> postSideEffect(MapEffect.RefreshPhotoBooth)
            is MapIntent.UpdateCurrentLocation -> reduce { copy(currentLocation = intent.latitude to intent.longitude) }
            MapIntent.ClickCurrentLocation -> postSideEffect(MapEffect.RefreshCurrentLocation)
            MapIntent.ClickInfoIcon -> reduce { copy(isShowInfoDialog = true) }
            MapIntent.ClickCloseInfoIcon -> reduce { copy(isShowInfoDialog = false) }
            MapIntent.ClickToMapChip -> reduce { copy(dragLevel = DragLevel.FIRST) }
            is MapIntent.ClickBrand -> handleClickBrand(state, intent, reduce)
            is MapIntent.ClickNearBrand -> handleClickNearBrand(intent, reduce, postSideEffect)
            MapIntent.ClickCloseBrandCard -> reduce {
                copy(
                    dragLevel = DragLevel.SECOND,
                    focusedMarkerPosition = Pair(0.0, 0.0),
                    selectedBrandInfo = null,
                )
            }
            MapIntent.CloseDirectionBottomSheet -> reduce { copy(isShowDirectionBottomSheet = false) }
            is MapIntent.ClickDirectionItem -> handleClickDirectionItem(state, intent, reduce, postSideEffect)
            is MapIntent.ChangeDragLevel -> reduce { copy(dragLevel = intent.dragLevel) }
            is MapIntent.ClickBrandMarker -> handleClickBrandMarker(state, intent, reduce, postSideEffect)
            is MapIntent.ClickDirection -> reduce { copy(isShowDirectionBottomSheet = true) }
            is MapIntent.RequestLocationPermission -> handleRequestLocationPermission(intent, reduce, postSideEffect)
            MapIntent.DismissLocationPermissionDialog -> reduce { copy(isShowLocationPermissionDialog = false) }
            MapIntent.ConfirmLocationPermissionDialog -> {
                reduce { copy(isShowLocationPermissionDialog = false) }
                postSideEffect(MapEffect.NavigateToAppSettings)
            }
        }
    }

    private fun handleClickBrand(
        state: MapState,
        intent: MapIntent.ClickBrand,
        reduce: (MapState.() -> MapState) -> Unit,
    ) {
        reduce {
            copy(
                brands = state.brands.map { brand ->
                    if (brand == intent.brand) {
                        brand.copy(isChecked = !brand.isChecked)
                    } else {
                        brand
                    }
                }.toImmutableList(),
            )
        }
    }

    private fun handleClickNearBrand(
        intent: MapIntent.ClickNearBrand,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce {
            copy(
                dragLevel = DragLevel.INVISIBLE,
                selectedBrandInfo = intent.brandInfo,
                focusedMarkerPosition = intent.brandInfo.latitude to intent.brandInfo.longitude,
            )
        }
        postSideEffect(MapEffect.MoveCameraToPosition(intent.brandInfo.latitude, intent.brandInfo.longitude))
    }

    private fun handleClickDirectionItem(
        state: MapState,
        intent: MapIntent.ClickDirectionItem,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        reduce { copy(isShowDirectionBottomSheet = false) }
        state.selectedBrandInfo?.let { brandInfo ->
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
        intent: MapIntent.ClickBrandMarker,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        val selectedBrand = state.nearbyBrands.find { it.latitude == intent.latitude && it.longitude == intent.longitude }
        reduce {
            copy(
                dragLevel = DragLevel.INVISIBLE,
                focusedMarkerPosition = intent.latitude to intent.longitude,
                selectedBrandInfo = selectedBrand,
            )
        }
        postSideEffect(MapEffect.MoveCameraToPosition(intent.latitude, intent.longitude))
    }

    private fun handleRequestLocationPermission(
        intent: MapIntent.RequestLocationPermission,
        reduce: (MapState.() -> MapState) -> Unit,
        postSideEffect: (MapEffect) -> Unit,
    ) {
        viewModelScope.launch {
            val isFirstRequest = dataStoreRepository.getBoolean(DataStoreKey.IS_FIRST_LOCATION_PERMISSION).first().not()

            when {
                isFirstRequest -> {
                    Timber.d("최초 요청 - 시스템 권한 팝업 표시")
                    dataStoreRepository.setBoolean(
                        DataStoreKey.IS_FIRST_LOCATION_PERMISSION,
                        true,
                    )
                    postSideEffect(MapEffect.RequestLocationPermission)
                }

                intent.shouldShowRationale -> {
                    Timber.d("1회 거부 상태 - 시스템 권한 팝업 표시")
                    postSideEffect(MapEffect.RequestLocationPermission)
                }

                else -> {
                    Timber.d("2회 이상 거부 (영구 거부) - 설정 이동 다이얼로그 표시")
                    reduce { copy(isShowLocationPermissionDialog = true) }
                }
            }
        }
    }

    private fun loadBrands(reduce: (MapState.() -> MapState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            val brands = persistentListOf(
                Brand(isChecked = false, brandName = "인생네컷", brandImageRes = R.drawable.icon_life_four_cut),
                Brand(isChecked = false, brandName = "포토그레이", brandImageRes = R.drawable.icon_photogray),
                Brand(isChecked = false, brandName = "포토이즘", brandImageRes = R.drawable.icon_photoism),
                Brand(isChecked = false, brandName = "하루필름", brandImageRes = R.drawable.icon_haru_film),
                Brand(isChecked = false, brandName = "플랜비\n스튜디오", brandImageRes = R.drawable.icon_planb_studio),
                Brand(isChecked = false, brandName = "포토시그니처", brandImageRes = R.drawable.icon_photo_signature),
            )

            // 중심: 37.5270539, 126.8862648 주변 100m 이내
            val nearbyBrands = persistentListOf(
                BrandInfo(
                    brandName = "인생네컷",
                    brandImageRes = R.drawable.icon_life_four_cut,
                    branchName = "가산디지털점",
                    distance = "25m",
                    latitude = 37.5272,
                    longitude = 126.8864,
                ),
                BrandInfo(
                    brandName = "포토그레이",
                    brandImageRes = R.drawable.icon_photogray,
                    branchName = "가산역점",
                    distance = "38m",
                    latitude = 37.5248,
                    longitude = 126.8867,
                ),
                BrandInfo(
                    brandName = "포토이즘",
                    brandImageRes = R.drawable.icon_photoism,
                    branchName = "마리오점",
                    distance = "52m",
                    latitude = 37.5274,
                    longitude = 126.8828,
                ),
                BrandInfo(
                    brandName = "하루필름",
                    brandImageRes = R.drawable.icon_haru_film,
                    branchName = "W몰점",
                    distance = "65m",
                    latitude = 37.5166,
                    longitude = 126.8659,
                ),
                BrandInfo(
                    brandName = "플랜비스튜디오",
                    brandImageRes = R.drawable.icon_planb_studio,
                    branchName = "대륭포스트점",
                    distance = "72m",
                    latitude = 37.5176,
                    longitude = 126.8969,
                ),
                BrandInfo(
                    brandName = "포토시그니처",
                    brandImageRes = R.drawable.icon_photo_signature,
                    branchName = "에이스점",
                    distance = "80m",
                    latitude = 37.5264,
                    longitude = 126.8865,
                ),
                BrandInfo(
                    brandName = "인생네컷",
                    brandImageRes = R.drawable.icon_life_four_cut,
                    branchName = "IT캐슬점",
                    distance = "88m",
                    latitude = 37.5278,
                    longitude = 126.8860,
                ),
                BrandInfo(
                    brandName = "포토그레이",
                    brandImageRes = R.drawable.icon_photogray,
                    branchName = "벽산점",
                    distance = "95m",
                    latitude = 37.5263,
                    longitude = 126.8856,
                ),
            )

            reduce {
                copy(
                    isLoading = false,
                    brands = brands,
                    nearbyBrands = nearbyBrands,
                )
            }
        }
    }
}
