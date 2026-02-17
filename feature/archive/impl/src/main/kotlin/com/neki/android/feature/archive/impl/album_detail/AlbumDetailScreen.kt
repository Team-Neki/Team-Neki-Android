package com.neki.android.feature.archive.impl.album_detail

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.DoubleButtonOptionBottomSheet
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.album_detail.component.AlbumDetailTopBar
import com.neki.android.feature.archive.impl.album_detail.component.RenameAlbumBottomSheet
import com.neki.android.feature.archive.impl.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.component.EmptyAlbumContent
import com.neki.android.feature.archive.impl.component.SelectablePhotoItem
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_ALBUM_NAME_MAX_LENGTH
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRAY_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRID_LAYOUT_HORIZONTAL_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRID_LAYOUT_TOP_PADDING
import com.neki.android.feature.archive.impl.model.SelectMode
import com.neki.android.feature.archive.impl.photo.component.PhotoActionBar
import com.neki.android.feature.archive.impl.util.ImageDownloader
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

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
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.store.onIntent(AlbumDetailIntent.SelectGalleryImage(uris))
        } else {
            Timber.d("No media selected")
        }
    }

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

            AlbumDetailSideEffect.OpenGallery -> photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            AlbumDetailSideEffect.RefreshPhotos -> pagingItems.refresh()
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

    val isRefreshing by remember {
        derivedStateOf { pagingItems.loadState.refresh is LoadState.Loading }
    }
    val isEmpty by remember {
        derivedStateOf { pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading }
    }

    BackHandler(enabled = true) {
        onIntent(AlbumDetailIntent.OnBackPressed)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        AlbumDetailTopBar(
            isFavoriteAlbum = uiState.isFavoriteAlbum,
            title = if (uiState.isFavoriteAlbum) "즐겨찾기" else uiState.title,
            selectMode = uiState.selectMode,
            showOptionPopup = uiState.isShowOptionPopup,
            hasNoPhoto = isEmpty,
            onClickBack = { onIntent(AlbumDetailIntent.ClickBackIcon) },
            onClickOptionIcon = { onIntent(AlbumDetailIntent.ClickOptionIcon) },
            onClickSelect = { onIntent(AlbumDetailIntent.ClickSelectOption) },
            onClickAddPhoto = { onIntent(AlbumDetailIntent.ClickAddPhotoOption) },
            onClickRenameAlbum = { onIntent(AlbumDetailIntent.ClickRenameAlbumOption) },
            onClickCancel = { onIntent(AlbumDetailIntent.ClickCancelButton) },
            onDismissPopup = { onIntent(AlbumDetailIntent.DismissOptionPopup) },
        )

        if (isEmpty) {
            EmptyAlbumContent()
        } else {
            AlbumDetailContent(
                uiState = uiState,
                pagingItems = pagingItems,
                lazyState = lazyState,
                onIntent = onIntent,
            )
        }
    }

    if (isRefreshing || uiState.isLoading) {
        LoadingDialog()
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

    if (uiState.isShowRenameAlbumBottomSheet) {
        val errorMessage by remember(uiState.renameAlbumTextState.text) {
            derivedStateOf {
                val newName = uiState.renameAlbumTextState.text.toString()
                when {
                    newName == uiState.title -> "이미 사용 중인 앨범명이에요."
                    newName.length > ARCHIVE_ALBUM_NAME_MAX_LENGTH -> "앨범명은 최대 10자까지 가능해요."
                    // TODO: 이미 존재하는 앨범명 중복처리
                    else -> null
                }
            }
        }
        RenameAlbumBottomSheet(
            textFieldState = uiState.renameAlbumTextState,
            onDismissRequest = { onIntent(AlbumDetailIntent.DismissRenameBottomSheet) },
            onClickCancel = { onIntent(AlbumDetailIntent.ClickRenameBottomSheetCancelButton) },
            onClickConfirm = { onIntent(AlbumDetailIntent.ClickRenameBottomSheetConfirmButton) },
            isError = errorMessage != null,
            errorMessage = errorMessage,
        )
    }
}

@Composable
internal fun AlbumDetailContent(
    uiState: AlbumDetailState,
    pagingItems: LazyPagingItems<Photo>,
    lazyState: LazyStaggeredGridState,
    modifier: Modifier = Modifier,
    onIntent: (AlbumDetailIntent) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
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
}

@Preview
@Composable
private fun AlbumDetailScreenPreview() {
    val photos = (0..10).map {
        Photo(
            id = it.toLong(),
            imageUrl = "",
            isFavorite = false,
            date = "2024-04-2$it",
        )
    }

    val pagingData = PagingData.from(photos)
    val pagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                title = "앨범 상세",
            ),
            pagingItems = pagingItems,
        )
    }
}

@Preview
@Composable
private fun AlbumDetailScreenEmptyPreview() {
    val pagingData = PagingData.from(emptyList<Photo>())
    val pagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                title = "빈 앨범",
            ),
            pagingItems = pagingItems,
        )
    }
}

@Preview
@Composable
private fun AlbumDetailScreenFavoritePreview() {
    val photos = (0..5).map {
        Photo(
            id = it.toLong(),
            imageUrl = "",
            isFavorite = true,
            date = "2024-04-2$it",
        )
    }

    val pagingData = PagingData.from(photos)
    val pagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                title = "즐겨찾기",
                isFavoriteAlbum = true,
            ),
            pagingItems = pagingItems,
        )
    }
}

@Preview
@Composable
private fun AlbumDetailScreenSelectModePreview() {
    val photos = (0..10).map {
        Photo(
            id = it.toLong(),
            imageUrl = "",
            isFavorite = false,
            date = "2024-04-2$it",
        )
    }

    val pagingData = PagingData.from(photos)
    val pagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                title = "앨범 상세",
                selectMode = SelectMode.SELECTING,
                selectedPhotos = persistentListOf(photos[0], photos[2], photos[4]),
            ),
            pagingItems = pagingItems,
        )
    }
}

@Preview
@Composable
private fun AlbumDetailScreenOptionPopupPreview() {
    val photos = (0..5).map {
        Photo(
            id = it.toLong(),
            imageUrl = "",
            isFavorite = false,
            date = "2024-04-2$it",
        )
    }

    val pagingData = PagingData.from(photos)
    val pagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                title = "앨범 상세",
                isShowOptionPopup = true,
            ),
            pagingItems = pagingItems,
        )
    }
}
