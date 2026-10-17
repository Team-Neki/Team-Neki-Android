package com.neki.android.feature.map.impl.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.LocLatLng
import com.neki.android.feature.map.impl.MapIntent
import com.neki.android.feature.map.impl.component.MapSearchBar
import com.neki.android.feature.map.impl.component.PhotoBoothDetailContent
import com.neki.android.feature.map.impl.region.component.BrandFilterBottomSheet
import com.neki.android.feature.map.impl.region.component.SearchResultPhotoBoothPanel
import com.neki.android.feature.map.impl.search.model.SearchBrandFilter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun SearchResultOverlay(
    state: SearchResultPanelState,
    mapMarkers: ImmutableList<PhotoBooth>,
    brands: ImmutableList<Brand>,
    showDistance: Boolean,
    isCurrentLocation: Boolean,
    onIntent: (MapIntent) -> Unit,
) {
    val visibleBooths = state.visibleBooths(brands)
    val focusedBooth = mapMarkers.find { it.id == state.focusedBoothId }
        ?: visibleBooths.find { it.id == state.focusedBoothId }
    if (focusedBooth == null) {
        SearchResultPhotoBoothPanel(
            photoBooths = visibleBooths,
            selectedBrandNames = brands.filter { it.id in state.selectedBrandIds }.map { it.name }.toImmutableList(),
            dragLevel = state.dragLevel,
            showDistance = showDistance,
            isCurrentLocation = isCurrentLocation,
            onDragLevelChanged = { onIntent(MapIntent.SearchPanel(SearchResultPanelIntent.ChangeDragLevel(it))) },
            onCurrentLocationClick = { onIntent(MapIntent.ClickCurrentLocationIcon) },
            onFilterClick = { onIntent(MapIntent.SearchPanel(SearchResultPanelIntent.OpenFilter)) },
            onBoothClick = { onIntent(MapIntent.ClickPhotoBoothListItem(it)) },
            onFavoriteClick = { onIntent(MapIntent.ClickPhotoBoothFavorite(it)) },
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            PhotoBoothDetailContent(
                photoBooth = focusedBooth,
                modifier = Modifier.align(Alignment.BottomCenter),
                isFavorite = focusedBooth.favorite,
                showDistance = showDistance,
                onClickFavorite = { onIntent(MapIntent.ClickPhotoBoothFavorite(focusedBooth)) },
                onClickCloseCard = { onIntent(MapIntent.ClickClosePhotoBoothCard) },
                onClickCard = {
                    onIntent(MapIntent.ClickPhotoBoothCard(LocLatLng(focusedBooth.latitude, focusedBooth.longitude)))
                },
                onClickDirection = { onIntent(MapIntent.ClickDirectionIcon) },
            )
        }
    }
    if (state.showFilter) {
        BrandFilterBottomSheet(
            filters = brands.mapNotNull { brand ->
                val count = state.photoBooths.count { it.brandName == brand.name }
                if (count > 0) SearchBrandFilter(brand = brand, count = count) else null
            }.toImmutableList(),
            selectedBrandIds = state.draftBrandIds,
            onDismiss = { onIntent(MapIntent.SearchPanel(SearchResultPanelIntent.DismissFilter)) },
            onBrandClick = { onIntent(MapIntent.SearchPanel(SearchResultPanelIntent.ToggleBrand(it))) },
            onApply = { onIntent(MapIntent.SearchPanel(SearchResultPanelIntent.ApplyFilter)) },
        )
    }
}

private val PreviewSearchBooths = persistentListOf(
    PhotoBooth(id = "source:LIFEFOURCUTS:1", brandName = "인생네컷", branchName = "사당역점", address = "서울 동작구 동작대로 27", distance = 150),
    PhotoBooth(id = "source:PHOTOGRAY:1", brandName = "포토그레이", branchName = "사당점", address = "서울 동작구 동작대로 43", distance = 300, favorite = true),
)

@ComponentPreview
@Composable
private fun SearchResultOverlayResultsPreview() {
    SearchResultOverlayPreviewContent(
        state = SearchResultPanelState(keyword = "사당", photoBooths = PreviewSearchBooths),
    )
}

@ComponentPreview
@Composable
private fun SearchResultOverlayEmptyPreview() {
    SearchResultOverlayPreviewContent(
        state = SearchResultPanelState(keyword = "사당"),
        showDistance = false,
    )
}

@ComponentPreview
@Composable
private fun SearchResultOverlayDetailPreview() {
    SearchResultOverlayPreviewContent(
        state = SearchResultPanelState(keyword = "사당", photoBooths = PreviewSearchBooths, focusedBoothId = "source:PHOTOGRAY:1"),
    )
}

@ComponentPreview
@Composable
private fun SearchResultOverlayMarkerDetailPreview() {
    SearchResultOverlayPreviewContent(
        state = SearchResultPanelState(
            keyword = "사당",
            photoBooths = persistentListOf(PreviewSearchBooths.first()),
            focusedBoothId = "source:PHOTOGRAY:1",
        ),
        mapMarkers = PreviewSearchBooths,
    )
}

@ComponentPreview
@Composable
private fun SearchResultOverlayFilteredPreview() {
    SearchResultOverlayPreviewContent(
        state = SearchResultPanelState(keyword = "사당", photoBooths = PreviewSearchBooths, selectedBrandIds = persistentSetOf(2L)),
    )
}

@Composable
private fun SearchResultOverlayPreviewContent(
    state: SearchResultPanelState,
    showDistance: Boolean = true,
    mapMarkers: ImmutableList<PhotoBooth> = persistentListOf(),
) {
    NekiTheme {
        // 지도 SDK 없이 검색바와 결과 오버레이의 배치를 확인한다.
        Box(
            modifier = Modifier
                .size(width = 375.dp, height = 760.dp)
                .background(NekiTheme.colorScheme.gray50),
        ) {
            MapSearchBar(
                query = state.keyword,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            )
            SearchResultOverlay(
                state = state,
                mapMarkers = mapMarkers,
                brands = persistentListOf(Brand(id = 1, name = "인생네컷"), Brand(id = 2, name = "포토그레이")),
                showDistance = showDistance,
                isCurrentLocation = false,
                onIntent = {},
            )
        }
    }
}
