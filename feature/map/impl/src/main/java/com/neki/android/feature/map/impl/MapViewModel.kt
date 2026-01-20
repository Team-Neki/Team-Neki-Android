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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    val store: MviIntentStore<MapState, MapIntent, MapEffect> =
        mviIntentStore(
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
            MapIntent.EnterMapScreen -> { loadBrands(reduce) }
            MapIntent.ClickCurrentLocation -> { postSideEffect(MapEffect.RefreshCurrentLocation) }
            MapIntent.ClickInfoIcon -> { reduce { copy(isShowInfoDialog = true) } }
            MapIntent.ClickCloseInfoIcon -> { reduce { copy(isShowInfoDialog = false) } }
            MapIntent.ClickToMapChip -> { reduce { copy(dragState = DragValue.Bottom) } }
            MapIntent.ClickBrand -> { }
            MapIntent.ClickNearBrand -> { reduce { copy(dragState = DragValue.Invisible) } }
            MapIntent.ClickCloseBrandCard -> { reduce { copy(dragState = DragValue.Center, focusedMarkerPosition = Pair(0.0, 0.0)) } }
            MapIntent.CloseDirectionBottomSheet -> { reduce { copy(isShowDirectionBottomSheet = false) } }
            is MapIntent.ClickDirectionItem -> {
                reduce { copy(isShowDirectionBottomSheet = false) }
                postSideEffect(MapEffect.MoveDirectionApp(intent.app))
            }
            is MapIntent.ChangeDragValue -> { reduce { copy(dragState = intent.dragValue) } }
            is MapIntent.ClickBrandMarker -> {
                reduce { copy(dragState = DragValue.Invisible, focusedMarkerPosition = intent.latitude to intent.longitude) }
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
            val nearbyBrands = persistentListOf(
                BrandInfo(brandName = "인생네컷", brandImageRes = R.drawable.icon_life_four_cut, branchName = "사당역점", distance = "320m"),
                BrandInfo(brandName = "포토그레이", brandImageRes = R.drawable.icon_photogray, branchName = "강남역점", distance = "450m"),
                BrandInfo(brandName = "포토이즘", brandImageRes = R.drawable.icon_photoism, branchName = "홍대입구점", distance = "580m"),
                BrandInfo(brandName = "하루필름", brandImageRes = R.drawable.icon_haru_film, branchName = "신촌점", distance = "720m"),
                BrandInfo(brandName = "플랜비스튜디오", brandImageRes = R.drawable.icon_planb_studio, branchName = "잠실역점", distance = "890m"),
                BrandInfo(brandName = "포토시그니처", brandImageRes = R.drawable.icon_photo_signature, branchName = "건대입구점", distance = "1.2km"),
                BrandInfo(brandName = "포토시그니처11", brandImageRes = R.drawable.icon_photo_signature, branchName = "건대입구점", distance = "1.2km"),
                BrandInfo(brandName = "포토시그니처22", brandImageRes = R.drawable.icon_photo_signature, branchName = "건대입구점", distance = "1.2km"),
                BrandInfo(brandName = "포토시그니처333", brandImageRes = R.drawable.icon_photo_signature, branchName = "건대입구점", distance = "1.2km"),
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
