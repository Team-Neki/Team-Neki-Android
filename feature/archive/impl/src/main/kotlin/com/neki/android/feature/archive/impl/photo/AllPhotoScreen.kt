package com.neki.android.feature.archive.impl.photo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.component.SelectablePhotoItem
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRAY_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRID_LAYOUT_HORIZONTAL_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRID_LAYOUT_TOP_PADDING
import com.neki.android.feature.archive.impl.model.SelectMode
import com.neki.android.feature.archive.impl.photo.component.AllPhotoFilterBar
import com.neki.android.feature.archive.impl.photo.component.AllPhotoTopBar
import com.neki.android.feature.archive.impl.photo.component.PhotoActionBar
import com.neki.android.feature.archive.impl.util.ImageDownloader
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
internal fun AllPhotoRoute(
    viewModel: AllPhotoViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPhotoDetail: (Photo) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.photoPagingData.collectAsLazyPagingItems()
    Timber.d(pagingItems.toString())
    val context = LocalContext.current
    val lazyState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            AllPhotoSideEffect.NavigateBack -> navigateBack()
            AllPhotoSideEffect.ScrollToTop -> coroutineScope.launch {
                snapshotFlow { pagingItems.loadState.refresh }
                    .dropWhile { it is LoadState.NotLoading }  // 해당 이벤트 도착이, 새로운 pagingItems 조회보다 빠름.
                    .first { it is LoadState.NotLoading }
                lazyState.scrollToItem(0)
            }

            is AllPhotoSideEffect.NavigateToPhotoDetail -> navigateToPhotoDetail(sideEffect.photo)
            is AllPhotoSideEffect.ShowToastMessage -> {
                nekiToast.showToast(text = sideEffect.message)
            }

            is AllPhotoSideEffect.DownloadImages -> {
                ImageDownloader.downloadImages(context, sideEffect.imageUrls)
                    .onSuccess {
                        nekiToast.showToast(text = "사진을 갤러리에 다운로드했어요")
                    }
                    .onFailure {
                        nekiToast.showToast(text = "다운로드에 실패했어요")
                    }
            }
        }
    }

    AllPhotoScreen(
        uiState = uiState,
        pagingItems = pagingItems,
        lazyState = lazyState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun AllPhotoScreen(
    uiState: AllPhotoState,
    pagingItems: LazyPagingItems<Photo>,
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

    val isRefreshing by remember { derivedStateOf { pagingItems.loadState.refresh is LoadState.Loading } }

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
            onClickBack = { onIntent(AllPhotoIntent.ClickTopBarBackIcon) },
            onClickSelect = { onIntent(AllPhotoIntent.ClickTopBarSelectIcon) },
            onClickCancel = { onIntent(AllPhotoIntent.ClickTopBarCancelIcon) },
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
                    top = if (uiState.selectMode == SelectMode.SELECTING) PHOTO_GRID_LAYOUT_TOP_PADDING.dp else topPadding + PHOTO_GRID_LAYOUT_TOP_PADDING.dp,
                    start = PHOTO_GRID_LAYOUT_HORIZONTAL_PADDING.dp,
                    end = PHOTO_GRID_LAYOUT_HORIZONTAL_PADDING.dp,
                    bottom = PHOTO_GRAY_LAYOUT_BOTTOM_PADDING.dp,
                ),
                verticalItemSpacing = ARCHIVE_GRID_ITEM_SPACING.dp,
                horizontalArrangement = Arrangement.spacedBy(ARCHIVE_GRID_ITEM_SPACING.dp),
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { it.id },
                ) { index ->
                    val photo = pagingItems[index]
                    if (photo != null) {
                        val isSelected = uiState.selectedPhotos.any { it.id == photo.id }
                        SelectablePhotoItem(
                            photo = photo,
                            isSelected = isSelected,
                            isSelectMode = uiState.selectMode == SelectMode.SELECTING,
                            onClickItem = { onIntent(AllPhotoIntent.ClickPhotoItem(photo)) },
                            onClickSelect = { onIntent(AllPhotoIntent.ClickPhotoItem(photo)) },
                        )
                    }
                }

                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = NekiTheme.colorScheme.primary500,
                            )
                        }
                    }
                }
            }

            AllPhotoFilterBar(
                showFilterPopup = uiState.isShowFilterDialog,
                modifier = Modifier
                    .onSizeChanged { size ->
                        if (filterBarHeightPx != 0) return@onSizeChanged
                        filterBarHeightPx = size.height
                    },
                selectedFilter = uiState.selectedPhotoFilter,
                isFavoriteSelected = uiState.isFavoriteChipSelected,
                visible = uiState.selectMode == SelectMode.DEFAULT && showFilterBar,
                onClickSortChip = { onIntent(AllPhotoIntent.ClickFilterChip) },
                onClickFavoriteChip = { onIntent(AllPhotoIntent.ClickFavoriteFilterChip) },
                onDismissPopup = { onIntent(AllPhotoIntent.DismissFilterPopup) },
                onClickFilterRow = { onIntent(AllPhotoIntent.ClickFilterPopupRow(it)) },
            )
        }

        PhotoActionBar(
            visible = uiState.selectMode == SelectMode.SELECTING,
            isEnabled = uiState.selectedPhotos.isNotEmpty(),
            onClickDownload = { onIntent(AllPhotoIntent.ClickDownloadIcon) },
            onClickDelete = { onIntent(AllPhotoIntent.ClickDeleteIcon) },
        )
    }

    if (isRefreshing || uiState.isLoading) {
        LoadingDialog()
    }

    if (uiState.isShowDeleteDialog) {
        DeletePhotoDialog(
            onDismissRequest = { onIntent(AllPhotoIntent.DismissDeleteDialog) },
            onClickDelete = { onIntent(AllPhotoIntent.ClickDeleteDialogConfirmButton) },
            onClickCancel = { onIntent(AllPhotoIntent.DismissDeleteDialog) },
        )
    }
}

@DevicePreview
@Composable
private fun AllPhotoScreenPreview() {
    NekiTheme {
        // Preview는 Paging 없이 간단히 표시
    }
}
