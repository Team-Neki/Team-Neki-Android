package com.neki.android.feature.archive.impl.album_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.DoubleButtonOptionBottomSheet
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.album_detail.component.EmptyContent
import com.neki.android.feature.archive.impl.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.component.SelectablePhotoItem
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRAY_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRID_LAYOUT_HORIZONTAL_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRID_LAYOUT_TOP_PADDING
import com.neki.android.feature.archive.impl.model.SelectMode
import com.neki.android.feature.archive.impl.photo.component.PhotoActionBar
import com.neki.android.feature.archive.impl.util.ImageDownloader
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun AlbumDetailRoute(
    viewModel: AlbumDetailViewModel,
    navigateBack: () -> Unit,
    navigateToPhotoDetail: (Photo) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.photoPagingData.collectAsLazyPagingItems()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            AlbumDetailSideEffect.NavigateBack -> navigateBack()
            is AlbumDetailSideEffect.NavigateToPhotoDetail -> navigateToPhotoDetail(sideEffect.photo)
            is AlbumDetailSideEffect.ShowToastMessage -> {
                nekiToast.showToast(text = sideEffect.message)
            }

            is AlbumDetailSideEffect.DownloadImages -> {
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

    AlbumDetailScreen(
        uiState = uiState,
        pagingItems = pagingItems,
        onIntent = viewModel.store::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailScreen(
    uiState: AlbumDetailState,
    pagingItems: LazyPagingItems<Photo>,
    onIntent: (AlbumDetailIntent) -> Unit = {},
) {
    val lazyState = rememberLazyStaggeredGridState()

    val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading
    val isEmpty = pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading

    BackHandler(enabled = true) {
        onIntent(AlbumDetailIntent.OnBackPressed)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        AlbumDetailTopBar(
            hasNoPhoto = isEmpty,
            title = if (uiState.isFavoriteAlbum) "즐겨찾기" else uiState.title,
            selectMode = uiState.selectMode,
            onClickBack = { onIntent(AlbumDetailIntent.ClickBackIcon) },
            onClickSelect = { onIntent(AlbumDetailIntent.ClickSelectButton) },
            onClickCancel = { onIntent(AlbumDetailIntent.ClickCancelButton) },
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
                    top = PHOTO_GRID_LAYOUT_TOP_PADDING.dp,
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
                            onClickItem = { onIntent(AlbumDetailIntent.ClickPhotoItem(photo)) },
                            onClickSelect = { onIntent(AlbumDetailIntent.ClickPhotoItem(photo)) },
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
        }

        PhotoActionBar(
            visible = uiState.selectMode == SelectMode.SELECTING,
            isEnabled = uiState.selectedPhotos.isNotEmpty(),
            onClickDownload = { onIntent(AlbumDetailIntent.ClickDownloadIcon) },
            onClickDelete = { onIntent(AlbumDetailIntent.ClickDeleteIcon) },
        )
    }

    if (isRefreshing || uiState.isLoading) {
        LoadingDialog()
    }

    if (isEmpty) {
        EmptyContent(
            isFavorite = uiState.isFavoriteAlbum,
        )
    }

    if (uiState.isShowDeleteDialog) {
        DeletePhotoDialog(
            onDismissRequest = { onIntent(AlbumDetailIntent.DismissDeleteDialog) },
            onClickDelete = { onIntent(AlbumDetailIntent.ClickDeleteDialogConfirmButton) },
            onClickCancel = { onIntent(AlbumDetailIntent.ClickDeleteDialogCancelButton) },
        )
    }

    if (uiState.isShowDeleteBottomSheet) {
        DoubleButtonOptionBottomSheet(
            title = "사진을 삭제하시겠어요?",
            options = PhotoDeleteOption.entries.toImmutableList(),
            selectedOption = uiState.selectedDeleteOption,
            primaryButtonText = "삭제하기",
            secondaryButtonText = "취소",
            onDismissRequest = { onIntent(AlbumDetailIntent.DismissDeleteBottomSheet) },
            onClickSecondaryButton = { onIntent(AlbumDetailIntent.ClickDeleteBottomSheetCancelButton) },
            onClickPrimaryButton = { onIntent(AlbumDetailIntent.ClickDeleteBottomSheetConfirmButton) },
            onOptionSelect = { onIntent(AlbumDetailIntent.SelectDeleteOption(it)) },
        )
    }
}

@Composable
private fun AlbumDetailTopBar(
    title: String,
    selectMode: SelectMode,
    onClickBack: () -> Unit,
    onClickSelect: () -> Unit,
    onClickCancel: () -> Unit,
    modifier: Modifier = Modifier,
    hasNoPhoto: Boolean = false,
) {
    if (hasNoPhoto) {
        BackTitleTopBar(
            modifier = modifier,
            title = title,
            onBack = onClickBack,
        )
    } else {
        BackTitleTextButtonTopBar(
            modifier = modifier,
            title = title,
            buttonLabel = when (selectMode) {
                SelectMode.DEFAULT -> "선택"
                SelectMode.SELECTING -> "취소"
            },
            enabledTextColor = when (selectMode) {
                SelectMode.DEFAULT -> NekiTheme.colorScheme.primary500
                SelectMode.SELECTING -> NekiTheme.colorScheme.gray800
            },
            onBack = onClickBack,
            onClickTextButton = when (selectMode) {
                SelectMode.DEFAULT -> onClickSelect
                SelectMode.SELECTING -> onClickCancel
            },
        )
    }
}
