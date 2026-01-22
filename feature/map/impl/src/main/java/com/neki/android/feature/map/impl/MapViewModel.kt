package com.neki.android.feature.map.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.designsystem.R
import com.neki.android.core.model.Brand
import com.neki.android.core.model.BrandInfo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
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
            MapIntent.EnterMapScreen -> {
                loadBrands(reduce)
            }

            MapIntent.ClickCurrentLocation -> {
                postSideEffect(MapEffect.RefreshCurrentLocation)
            }

            MapIntent.ClickInfoIcon -> {
                reduce { copy(isShowInfoDialog = true) }
            }

            MapIntent.ClickCloseInfoIcon -> {
                reduce { copy(isShowInfoDialog = false) }
            }

            MapIntent.ClickToMapChip -> {
                reduce { copy(dragState = DragValue.Bottom) }
            }

            is MapIntent.ClickBrand -> {
                reduce {
                    copy(
                        brands = state.brands.map { brand ->
                            if (brand == intent.brand) {
                                brand.copy(isChecked = !brand.isChecked)
                            } else {
                                brand
                            }
                        }.toImmutableList()
                    )
                }
            }
            is MapIntent.ClickNearBrand -> {
                reduce {
                    copy(
                        dragState = DragValue.Invisible,
                        selectedBrandInfo = intent.brandInfo,
                        focusedMarkerPosition = intent.brandInfo.latitude to intent.brandInfo.longitude,
                    )
                }
                postSideEffect(MapEffect.MoveCameraToPosition(intent.brandInfo.latitude, intent.brandInfo.longitude))
            }

            MapIntent.ClickCloseBrandCard -> {
                reduce { copy(dragState = DragValue.Center, focusedMarkerPosition = Pair(0.0, 0.0), selectedBrandInfo = null) }
            }

            MapIntent.CloseDirectionBottomSheet -> {
                reduce { copy(isShowDirectionBottomSheet = false) }
            }

            is MapIntent.ClickDirectionItem -> {
                reduce { copy(isShowDirectionBottomSheet = false) }
                postSideEffect(MapEffect.MoveDirectionApp(intent.app))
            }

            is MapIntent.ChangeDragValue -> {
                reduce { copy(dragState = intent.dragValue) }
            }

            is MapIntent.ClickBrandMarker -> {
                val selectedBrand = state.nearbyBrands.find { it.latitude == intent.latitude && it.longitude == intent.longitude }
                reduce {
                    copy(
                        dragState = DragValue.Invisible,
                        focusedMarkerPosition = intent.latitude to intent.longitude,
                        selectedBrandInfo = selectedBrand,
                    )
                }
                postSideEffect(MapEffect.MoveCameraToPosition(intent.latitude, intent.longitude))
            }

            is MapIntent.ClickDirection -> {
                reduce { copy(isShowDirectionBottomSheet = true) }
            }
        }
    }

    private fun loadBrands(reduce: (MapState.() -> MapState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            // TODO: 서버 API 연동 시 교체
            val brands = persistentListOf(
                Brand(isChecked = false, brandName = "인생네컷", brandImageRes = R.drawable.icon_life_four_cut),
                Brand(isChecked = false, brandName = "포토그레이", brandImageRes = R.drawable.icon_photogray),
                Brand(isChecked = false, brandName = "포토이즘", brandImageRes = R.drawable.icon_photoism),
                Brand(isChecked = false, brandName = "하루필름", brandImageRes = R.drawable.icon_haru_film),
                Brand(isChecked = false, brandName = "플랜비\n스튜디오", brandImageRes = R.drawable.icon_planb_studio),
                Brand(isChecked = false, brandName = "포토시그니처", brandImageRes = R.drawable.icon_photo_signature),
            )

            // TODO: 서버 API 연동 시 교체
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
                    latitude = 37.5268,
                    longitude = 126.8867,
                ),
                BrandInfo(
                    brandName = "포토이즘",
                    brandImageRes = R.drawable.icon_photoism,
                    branchName = "마리오점",
                    distance = "52m",
                    latitude = 37.5274,
                    longitude = 126.8858,
                ),
                BrandInfo(
                    brandName = "하루필름",
                    brandImageRes = R.drawable.icon_haru_film,
                    branchName = "W몰점",
                    distance = "65m",
                    latitude = 37.5266,
                    longitude = 126.8859,
                ),
                BrandInfo(
                    brandName = "플랜비스튜디오",
                    brandImageRes = R.drawable.icon_planb_studio,
                    branchName = "대륭포스트점",
                    distance = "72m",
                    latitude = 37.5276,
                    longitude = 126.8869,
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
