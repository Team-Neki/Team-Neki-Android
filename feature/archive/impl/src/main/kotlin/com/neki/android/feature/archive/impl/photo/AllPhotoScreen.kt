package com.neki.android.feature.archive.impl.photo

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.neki.android.core.designsystem.DevicePreview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.archive.impl.component.SelectablePhotoItem
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_LAYOUT_HORIZONTAL_PADDING
import com.neki.android.feature.archive.impl.model.SelectMode
import com.neki.android.feature.archive.impl.photo.component.AllPhotoFilterBar
import com.neki.android.feature.archive.impl.photo.component.AllPhotoTopBar
import com.neki.android.feature.archive.impl.photo.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.photo.component.PhotoActionBar
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
internal fun AllPhotoRoute(
    viewModel: AllPhotoViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPhotoDetail: (Photo) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lazyState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            AllPhotoSideEffect.NavigateBack -> navigateBack()
            AllPhotoSideEffect.ScrollToTop -> coroutineScope.launch { lazyState.animateScrollToItem(0) }
            is AllPhotoSideEffect.NavigateToPhotoDetail -> navigateToPhotoDetail(sideEffect.photo)
            is AllPhotoSideEffect.ShowToastMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is AllPhotoSideEffect.DownloadImage -> {
                // TODO: Implement download
            }
        }
    }

    AllPhotoScreen(
        uiState = uiState,
        lazyState = lazyState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun AllPhotoScreen(
    uiState: AllPhotoState = AllPhotoState(),
    lazyState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onIntent: (AllPhotoIntent) -> Unit = {},
) {
    val density = LocalDensity.current
    var filterBarHeightPx by remember { mutableIntStateOf(0) }
    val topPadding = with(density) { filterBarHeightPx.toDp() }
    val showFilterBar by remember {
        derivedStateOf {
            !lazyState.canScrollBackward ||
                lazyState.lastScrolledBackward ||
                uiState.selectMode == SelectMode.SELECTING
        }
    }

    BackHandler(enabled = true) {
        onIntent(AllPhotoIntent.OnBackPressed)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        AllPhotoTopBar(
            selectMode = uiState.selectMode,
            onBackClick = { onIntent(AllPhotoIntent.ClickTopBarBackIcon) },
            onSelectClick = { onIntent(AllPhotoIntent.ClickTopBarSelectIcon) },
            onCancelClick = { onIntent(AllPhotoIntent.ClickTopBarCancelIcon) },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Fixed(2),
                state = lazyState,
                contentPadding = PaddingValues(
                    top = if (uiState.selectMode == SelectMode.SELECTING) ARCHIVE_GRID_ITEM_SPACING.dp else topPadding + ARCHIVE_GRID_ITEM_SPACING.dp,
                    start = ARCHIVE_LAYOUT_HORIZONTAL_PADDING.dp,
                    end = ARCHIVE_LAYOUT_HORIZONTAL_PADDING.dp,
                    bottom = ARCHIVE_LAYOUT_BOTTOM_PADDING.dp,
                ),
                verticalItemSpacing = ARCHIVE_GRID_ITEM_SPACING.dp,
                horizontalArrangement = Arrangement.spacedBy(ARCHIVE_GRID_ITEM_SPACING.dp),
            ) {
                items(
                    items = uiState.showingPhotos,
                    key = { photo -> photo.id },
                ) { photo ->
                    val isSelected = uiState.selectedPhotos.any { it.id == photo.id }
                    SelectablePhotoItem(
                        photo = photo,
                        isSelected = isSelected,
                        isSelectMode = uiState.selectMode == SelectMode.SELECTING,
                        onItemClick = { onIntent(AllPhotoIntent.ClickPhotoItem(photo)) },
                        onSelectClick = { onIntent(AllPhotoIntent.ClickPhotoItem(photo)) },
                    )
                }
            }

            AllPhotoFilterBar(
                showFilterPopup = uiState.showFilterDialog,
                modifier = Modifier
                    .onSizeChanged { size ->
                        if (filterBarHeightPx != 0) return@onSizeChanged
                        filterBarHeightPx = size.height
                    },
                selectedFilter = uiState.selectedPhotoFilter,
                isFavoriteSelected = uiState.isFavoriteChipSelected,
                visible = uiState.selectMode == SelectMode.DEFAULT && showFilterBar,
                onSortChipClick = { onIntent(AllPhotoIntent.ClickFilterChip) },
                onFavoriteChipClick = { onIntent(AllPhotoIntent.ClickFavoriteFilterChip) },
                onDismissPopup = { onIntent(AllPhotoIntent.DismissFilterPopup) },
                onFilterRowClick = { onIntent(AllPhotoIntent.ClickFilterPopupRow(it)) },
            )
        }

        PhotoActionBar(
            visible = uiState.selectMode == SelectMode.SELECTING,
            isEnabled = uiState.selectedPhotos.isNotEmpty(),
            onDownloadClick = { onIntent(AllPhotoIntent.ClickDownloadIcon) },
            onDeleteClick = { onIntent(AllPhotoIntent.ClickDeleteIcon) },
        )
    }

    if (uiState.showDeleteDialog) {
        DeletePhotoDialog(
            onDismissRequest = { onIntent(AllPhotoIntent.DismissDeleteDialog) },
            onDeleteClick = { onIntent(AllPhotoIntent.ClickDeleteDialogConfirmButton) },
            onCancelClick = { onIntent(AllPhotoIntent.DismissDeleteDialog) },
        )
    }
}

@DevicePreview
@Composable
private fun AllPhotoScreenPreview() {
    val dummyPhotos = persistentListOf(
        Photo(id = 1, imageUrl = "https://picsum.photos/seed/all1/200/300", isFavorite = true),
        Photo(id = 2, imageUrl = "https://picsum.photos/seed/all2/200/250"),
        Photo(id = 3, imageUrl = "https://picsum.photos/seed/all3/200/350", isFavorite = true),
        Photo(id = 4, imageUrl = "https://picsum.photos/seed/all4/200/280"),
        Photo(id = 5, imageUrl = "https://picsum.photos/seed/all5/200/320", isFavorite = true),
        Photo(id = 6, imageUrl = "https://picsum.photos/seed/all6/200/260"),
        Photo(id = 7, imageUrl = "https://picsum.photos/seed/all7/200/290"),
        Photo(id = 8, imageUrl = "https://picsum.photos/seed/all8/200/310", isFavorite = true),
        Photo(id = 9, imageUrl = "https://picsum.photos/seed/all9/200/270"),
        Photo(id = 10, imageUrl = "https://picsum.photos/seed/all10/200/340"),
    )

    NekiTheme {
        AllPhotoScreen(
            uiState = AllPhotoState(
                photos = dummyPhotos,
            ),
        )
    }
}

@DevicePreview
@Composable
private fun AllPhotoScreenSelectingPreview() {
    val dummyPhotos = persistentListOf(
        Photo(id = 1, imageUrl = "https://picsum.photos/seed/sel1/200/300", isFavorite = true),
        Photo(id = 2, imageUrl = "https://picsum.photos/seed/sel2/200/250"),
        Photo(id = 3, imageUrl = "https://picsum.photos/seed/sel3/200/350", isFavorite = true),
        Photo(id = 4, imageUrl = "https://picsum.photos/seed/sel4/200/280"),
        Photo(id = 5, imageUrl = "https://picsum.photos/seed/sel5/200/320"),
        Photo(id = 6, imageUrl = "https://picsum.photos/seed/sel6/200/290"),
    )

    NekiTheme {
        AllPhotoScreen(
            uiState = AllPhotoState(
                photos = dummyPhotos,
                selectMode = SelectMode.SELECTING,
                selectedPhotos = persistentListOf(dummyPhotos[0], dummyPhotos[2], dummyPhotos[4]),
            ),
        )
    }
}
