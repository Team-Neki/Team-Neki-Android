package com.neki.android.feature.archive.impl.album_detail

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.archive.impl.album_detail.component.EmptyContent
import com.neki.android.feature.archive.impl.component.DeleteOptionBottomSheet
import com.neki.android.feature.archive.impl.component.SelectablePhotoItem
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_LAYOUT_HORIZONTAL_PADDING
import com.neki.android.feature.archive.impl.model.SelectMode
import com.neki.android.feature.archive.impl.photo.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.photo.component.PhotoActionBar
import com.neki.android.feature.archive.impl.util.ImageDownloader
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AlbumDetailRoute(
    viewModel: AlbumDetailViewModel,
    navigateBack: () -> Unit,
    navigateToPhotoDetail: (Photo) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            AlbumDetailSideEffect.NavigateBack -> navigateBack()
            is AlbumDetailSideEffect.NavigateToPhotoDetail -> navigateToPhotoDetail(sideEffect.photo)
            is AlbumDetailSideEffect.ShowToastMessage -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is AlbumDetailSideEffect.DownloadImages -> {
                ImageDownloader.downloadImages(context, sideEffect.imageUrls)
                    .onSuccess {
                        Toast.makeText(context, "사진을 갤러리에 다운로드했어요", Toast.LENGTH_SHORT).show()
                    }
                    .onFailure {
                        Toast.makeText(context, "다운로드에 실패했어요", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    AlbumDetailScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailScreen(
    uiState: AlbumDetailState = AlbumDetailState(),
    onIntent: (AlbumDetailIntent) -> Unit = {},
) {
    val lazyState = rememberLazyStaggeredGridState()

    BackHandler(enabled = true) {
        onIntent(AlbumDetailIntent.OnBackPressed)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        AlbumDetailTopBar(
            hasNoPhoto = uiState.album.photoList.isEmpty(),
            title = if (uiState.isFavoriteAlbum) "즐겨찾는 사진" else uiState.album.title,
            selectMode = uiState.selectMode,
            onBackClick = { onIntent(AlbumDetailIntent.ClickBackIcon) },
            onSelectClick = { onIntent(AlbumDetailIntent.ClickSelectButton) },
            onCancelClick = { onIntent(AlbumDetailIntent.ClickCancelButton) },
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
                    horizontal = ARCHIVE_LAYOUT_HORIZONTAL_PADDING.dp,
                    vertical = 8.dp,
                ),
                verticalItemSpacing = ARCHIVE_GRID_ITEM_SPACING.dp,
                horizontalArrangement = Arrangement.spacedBy(ARCHIVE_GRID_ITEM_SPACING.dp),
            ) {
                items(
                    items = uiState.album.photoList,
                    key = { photo -> photo.id },
                ) { photo ->
                    val isSelected = uiState.selectedPhotos.any { it.id == photo.id }
                    SelectablePhotoItem(
                        photo = photo,
                        isSelected = isSelected,
                        isSelectMode = uiState.selectMode == SelectMode.SELECTING,
                        onItemClick = { onIntent(AlbumDetailIntent.ClickPhotoItem(photo)) },
                        onSelectClick = { onIntent(AlbumDetailIntent.ClickPhotoItem(photo)) },
                    )
                }
            }
        }

        PhotoActionBar(
            visible = uiState.selectMode == SelectMode.SELECTING,
            isEnabled = uiState.selectedPhotos.isNotEmpty(),
            onDownloadClick = { onIntent(AlbumDetailIntent.ClickDownloadIcon) },
            onDeleteClick = { onIntent(AlbumDetailIntent.ClickDeleteIcon) },
        )
    }

    if (uiState.album.photoList.isEmpty()) {
        EmptyContent(
            isFavorite = uiState.isFavoriteAlbum,
        )
    }

    // Delete Dialog for Favorite Album
    if (uiState.showDeleteDialog) {
        DeletePhotoDialog(
            onDismissRequest = { onIntent(AlbumDetailIntent.DismissDeleteDialog) },
            onDeleteClick = { onIntent(AlbumDetailIntent.ClickDeleteDialogConfirmButton) },
            onCancelClick = { onIntent(AlbumDetailIntent.ClickDeleteDialogCancelButton) },
        )
    }

    // Delete BottomSheet for Regular Album
    if (uiState.showDeleteBottomSheet) {
        DeleteOptionBottomSheet(
            title = "사진을 삭제하시겠어요?",
            options = PhotoDeleteOption.entries,
            selectedOption = uiState.selectedDeleteOption,
            onDismissRequest = { onIntent(AlbumDetailIntent.DismissDeleteBottomSheet) },
            onCancelClick = { onIntent(AlbumDetailIntent.ClickDeleteBottomSheetCancelButton) },
            onDeleteClick = { onIntent(AlbumDetailIntent.ClickDeleteBottomSheetConfirmButton) },
            onOptionSelect = { onIntent(AlbumDetailIntent.SelectDeleteOption(it)) },
        )
    }
}

@Composable
private fun AlbumDetailTopBar(
    title: String,
    selectMode: SelectMode,
    onBackClick: () -> Unit,
    onSelectClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasNoPhoto: Boolean = false,
) {
    if (hasNoPhoto) {
        BackTitleTopBar(
            modifier = modifier,
            title = title,
            onBack = onBackClick,
        )
    } else {
        BackTitleTextButtonTopBar(
            modifier = modifier,
            title = title,
            buttonLabel = when (selectMode) {
                SelectMode.DEFAULT -> "선택"
                SelectMode.SELECTING -> "취소"
            },
            buttonLabelTextColor = when (selectMode) {
                SelectMode.DEFAULT -> NekiTheme.colorScheme.primary500
                SelectMode.SELECTING -> NekiTheme.colorScheme.gray800
            },
            onBack = onBackClick,
            onTextButtonClick = when (selectMode) {
                SelectMode.DEFAULT -> onSelectClick
                SelectMode.SELECTING -> onCancelClick
            },
        )
    }
}

@DevicePreview
@Composable
private fun AlbumDetailScreenFavoriteEmptyPreview() {
    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                album = Album(id = 0, title = "즐겨찾는 사진"),
                isFavoriteAlbum = true,
            ),
        )
    }
}

@DevicePreview
@Composable
private fun AlbumDetailScreenEmptyPreview() {
    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                album = Album(id = 0, title = "빈 앨범"),
                isFavoriteAlbum = true,
            ),
        )
    }
}

@DevicePreview
@Composable
private fun AlbumDetailScreenFavoritePreview() {
    val dummyPhotos = persistentListOf(
        Photo(id = 1, imageUrl = "https://picsum.photos/200/300", isFavorite = true),
        Photo(id = 2, imageUrl = "https://picsum.photos/200/250", isFavorite = true),
        Photo(id = 3, imageUrl = "https://picsum.photos/200/350", isFavorite = true),
    )

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                album = Album(id = 0, title = "즐겨찾는 사진", photoList = dummyPhotos),
                isFavoriteAlbum = true,
            ),
        )
    }
}

@DevicePreview
@Composable
private fun AlbumDetailScreenRegularPreview() {
    val dummyPhotos = persistentListOf(
        Photo(id = 1, imageUrl = "https://picsum.photos/200/300"),
        Photo(id = 2, imageUrl = "https://picsum.photos/200/250"),
        Photo(id = 3, imageUrl = "https://picsum.photos/200/350"),
    )

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                album = Album(id = 1, title = "네키 화이팅", photoList = dummyPhotos),
                isFavoriteAlbum = false,
            ),
        )
    }
}

@DevicePreview
@Composable
private fun AlbumDetailScreenSelectingPreview() {
    val dummyPhotos = persistentListOf(
        Photo(id = 1, imageUrl = "https://picsum.photos/200/300"),
        Photo(id = 2, imageUrl = "https://picsum.photos/200/250"),
        Photo(id = 3, imageUrl = "https://picsum.photos/200/350"),
    )

    NekiTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailState(
                album = Album(id = 1, title = "네키 화이팅", photoList = dummyPhotos),
                isFavoriteAlbum = false,
                selectMode = SelectMode.SELECTING,
                selectedPhotos = persistentListOf(dummyPhotos[1]),
            ),
        )
    }
}
