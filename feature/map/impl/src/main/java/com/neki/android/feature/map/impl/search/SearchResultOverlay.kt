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
import com.neki.android.core.model.SearchBrand
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
    state: SearchMapState,
    visibleBooths: ImmutableList<PhotoBooth>,
    brands: ImmutableList<Brand>,
    hasLocationPermission: Boolean,
    isCurrentLocation: Boolean,
    onIntent: (MapIntent) -> Unit,
) {
    val showDistance = state.showDistance && hasLocationPermission
    val focusedBooth = visibleBooths.find { it.id == state.focusedId }
    if (focusedBooth == null) {
        SearchResultPhotoBoothPanel(
            photoBooths = visibleBooths,
            selectedBrandNames = state.filters.filter { it.id in state.selectedBrandIds }.map { it.name }.toImmutableList(),
            dragLevel = state.dragLevel,
            isLoading = state.isLoading,
            isError = state.isError,
            showDistance = showDistance,
            isCurrentLocation = isCurrentLocation,
            onDragLevelChanged = { onIntent(MapIntent.SearchAction(SearchMapIntent.ChangeDragLevel(it))) },
            onCurrentLocationClick = { onIntent(MapIntent.ClickCurrentLocationIcon) },
            onFilterClick = { if (!state.isLoading && !state.isError) onIntent(MapIntent.SearchAction(SearchMapIntent.OpenFilter)) },
            onBoothClick = { onIntent(MapIntent.SearchAction(SearchMapIntent.SelectBooth(it.id, entryPoint = "bottom_sheet"))) },
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
                onClickCloseCard = { onIntent(MapIntent.SearchAction(SearchMapIntent.CloseBooth)) },
                onClickCard = {
                    onIntent(MapIntent.ClickPhotoBoothCard(LocLatLng(focusedBooth.latitude, focusedBooth.longitude)))
                },
                onClickDirection = { onIntent(MapIntent.ClickDirectionIcon) },
            )
        }
    }
    if (state.showFilter) {
        BrandFilterBottomSheet(
            filters = state.filters.map { filter ->
                SearchBrandFilter(
                    brand = brands.find { it.id == filter.id }
                        ?: Brand(id = filter.id, name = filter.name, code = filter.code),
                    count = filter.count,
                )
            }.toImmutableList(),
            selectedBrandIds = state.draftBrandIds,
            onDismiss = { onIntent(MapIntent.SearchAction(SearchMapIntent.DismissFilter)) },
            onBrandClick = { onIntent(MapIntent.SearchAction(SearchMapIntent.ToggleBrand(it))) },
            onApply = { onIntent(MapIntent.SearchAction(SearchMapIntent.ApplyFilter)) },
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
        state = SearchMapState(keyword = "사당", booths = PreviewSearchBooths, isLoading = false, showDistance = true),
    )
}

@ComponentPreview
@Composable
private fun SearchResultOverlayEmptyPreview() {
    SearchResultOverlayPreviewContent(
        state = SearchMapState(keyword = "사당", isLoading = false),
        showDistance = false,
    )
}

@ComponentPreview
@Composable
private fun SearchResultOverlayDetailPreview() {
    SearchResultOverlayPreviewContent(
        state = SearchMapState(
            keyword = "사당",
            booths = PreviewSearchBooths,
            focusedId = PreviewSearchBooths[1].id,
            isLoading = false,
            showDistance = true,
        ),
    )
}

@ComponentPreview
@Composable
private fun SearchResultOverlayFilteredPreview() {
    SearchResultOverlayPreviewContent(
        SearchMapState(
            keyword = "사당",
            booths = PreviewSearchBooths,
            filters = persistentListOf(SearchBrand(id = 2, name = "포토그레이", code = "", count = 1)),
            selectedBrandIds = persistentSetOf(2L),
            isLoading = false,
            showDistance = true,
        ),
    )
}

@Composable
private fun SearchResultOverlayPreviewContent(state: SearchMapState, showDistance: Boolean = true) {
    NekiTheme {
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
                visibleBooths = state.visibleBooths(),
                brands = persistentListOf(Brand(id = 1, name = "인생네컷"), Brand(id = 2, name = "포토그레이")),
                hasLocationPermission = showDistance,
                isCurrentLocation = false,
                onIntent = {},
            )
        }
    }
}
