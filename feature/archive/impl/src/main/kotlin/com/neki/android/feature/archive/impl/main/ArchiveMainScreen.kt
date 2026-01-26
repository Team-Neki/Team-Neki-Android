package com.neki.android.feature.archive.impl.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.component.AddAlbumBottomSheet
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.main.component.ArchiveMainAlbumList
import com.neki.android.feature.archive.impl.main.component.ArchiveMainPhotoItem
import com.neki.android.feature.archive.impl.main.component.ArchiveMainTitleRow
import com.neki.android.feature.archive.impl.main.component.ArchiveMainTopBar
import com.neki.android.feature.archive.impl.main.component.ChooseWithAlbumDialog
import com.neki.android.feature.archive.impl.main.component.GotoTopButton
import kotlinx.collections.immutable.persistentListOf
import timber.log.Timber

@Composable
internal fun ArchiveMainRoute(
    viewModel: ArchiveMainViewModel = hiltViewModel(),
    navigateToQRScan: () -> Unit,
    navigateToUploadAlbumWithGallery: (List<String>) -> Unit,
    navigateToUploadAlbumWithQRScan: (String) -> Unit,
    navigateToAllAlbum: () -> Unit,
    navigateToFavoriteAlbum: (Long) -> Unit,
    navigateToAlbumDetail: (Long) -> Unit,
    navigateToAllPhoto: () -> Unit,
    navigateToPhotoDetail: (Photo) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lazyState = rememberLazyStaggeredGridState()
    val nekiToast = remember { NekiToast(context) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.store.onIntent(ArchiveMainIntent.SelectGalleryImage(uris))
        } else {
            Timber.d("No media selected")
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            ArchiveMainSideEffect.NavigateToQRScan -> navigateToQRScan()
            is ArchiveMainSideEffect.NavigateToUploadAlbumWithGallery -> navigateToUploadAlbumWithGallery(sideEffect.uriStrings)
            is ArchiveMainSideEffect.NavigateToUploadAlbumWithQRScan -> navigateToUploadAlbumWithQRScan(sideEffect.imageUrl)
            ArchiveMainSideEffect.NavigateToAllAlbum -> navigateToAllAlbum()
            is ArchiveMainSideEffect.NavigateToFavoriteAlbum -> navigateToFavoriteAlbum(sideEffect.albumId)
            is ArchiveMainSideEffect.NavigateToAlbumDetail -> navigateToAlbumDetail(sideEffect.albumId)
            ArchiveMainSideEffect.NavigateToAllPhoto -> navigateToAllPhoto()
            is ArchiveMainSideEffect.NavigateToPhotoDetail -> navigateToPhotoDetail(sideEffect.photo)
            ArchiveMainSideEffect.ScrollToTop -> lazyState.animateScrollToItem(0)
            ArchiveMainSideEffect.OpenGallery -> photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            is ArchiveMainSideEffect.ShowToastMessage -> nekiToast.showToast(text = sideEffect.message)
        }
    }

    ArchiveMainScreen(
        lazyState = lazyState,
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun ArchiveMainScreen(
    lazyState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    uiState: ArchiveMainState = ArchiveMainState(),
    onIntent: (ArchiveMainIntent) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickableSingle { onIntent(ArchiveMainIntent.ClickScreen) },
    ) {
        Column {
            ArchiveMainTopBar(
                modifier = Modifier.padding(start = 20.dp, end = 8.dp),
                showTooltip = uiState.isFirstEntered,
                showAddPopup = uiState.isShowAddDialog,
                onClickPlusIcon = { onIntent(ArchiveMainIntent.ClickAddIcon) },
                onClickNotificationIcon = { onIntent(ArchiveMainIntent.ClickNotificationIcon) },
                onClickQRScan = { onIntent(ArchiveMainIntent.ClickQRScanRow) },
                onClickGallery = { onIntent(ArchiveMainIntent.ClickGalleryUploadRow) },
                onClickNewAlbum = { onIntent(ArchiveMainIntent.ClickAddNewAlbumRow) },
                onDismissPopup = { onIntent(ArchiveMainIntent.DismissAddDialog) },
            )
            ArchiveMainContent(
                uiState = uiState,
                lazyState = lazyState,
                onClickShowAllAlbum = { onIntent(ArchiveMainIntent.ClickAllAlbumText) },
                onClickFavoriteAlbum = { onIntent(ArchiveMainIntent.ClickFavoriteAlbum) },
                onClickAlbumItem = { onIntent(ArchiveMainIntent.ClickAlbumItem(it)) },
                onClickShowAllPhoto = { onIntent(ArchiveMainIntent.ClickAllPhotoText) },
                onClickPhotoItem = { photo -> onIntent(ArchiveMainIntent.ClickPhotoItem(photo)) },
            )
        }

        GotoTopButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            visible = !lazyState.isScrollInProgress && lazyState.firstVisibleItemIndex != 0,
            onClick = { onIntent(ArchiveMainIntent.ClickGoToTopButton) },
        )
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }

    if (uiState.isShowAddAlbumBottomSheet) {
        val textFieldState = rememberTextFieldState()
        val existingAlbumNames = remember { uiState.albums.map { it.title } }

        val errorMessage by remember(textFieldState.text) {
            derivedStateOf {
                val name = textFieldState.text.toString()
                when {
                    existingAlbumNames.contains(name) -> "이미 사용 중인 앨범명이에요."
                    else -> null
                }
            }
        }

        AddAlbumBottomSheet(
            textFieldState = textFieldState,
            onDismissRequest = { onIntent(ArchiveMainIntent.DismissAddAlbumBottomSheet) },
            onClickCancel = { onIntent(ArchiveMainIntent.DismissAddAlbumBottomSheet) },
            onClickConfirm = {
                val albumName = textFieldState.text.toString()
                if (errorMessage == null && albumName.isNotBlank()) {
                    onIntent(ArchiveMainIntent.ClickAddAlbumButton(albumName))
                }
            },
            isError = errorMessage != null,
            errorMessage = errorMessage,
        )
    }

    if (uiState.isShowChooseWithAlbumDialog) {
        ChooseWithAlbumDialog(
            onDismissRequest = { onIntent(ArchiveMainIntent.DismissChooseWithAlbumDialog) },
            onClickUploadWithOutAlbum = { onIntent(ArchiveMainIntent.ClickUploadWithoutAlbumRow) },
            onClickUploadWithAlbum = { onIntent(ArchiveMainIntent.ClickUploadWithAlbumRow) },
        )
    }
}

@Composable
private fun ArchiveMainContent(
    uiState: ArchiveMainState,
    lazyState: LazyStaggeredGridState,
    onClickShowAllAlbum: () -> Unit,
    onClickFavoriteAlbum: () -> Unit,
    onClickAlbumItem: (Long) -> Unit,
    onClickShowAllPhoto: () -> Unit,
    onClickPhotoItem: (Photo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }

    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxWidth(),
        columns = StaggeredGridCells.Fixed(2),
        state = lazyState,
        contentPadding = PaddingValues(
            bottom = ARCHIVE_LAYOUT_BOTTOM_PADDING.dp,
        ),
        verticalItemSpacing = ARCHIVE_GRID_ITEM_SPACING.dp,
        horizontalArrangement = Arrangement.spacedBy(ARCHIVE_GRID_ITEM_SPACING.dp),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = "앨범",
                textButtonTitle = "전체 보기",
                onClickShowAllAlbum = onClickShowAllAlbum,
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainAlbumList(
                modifier = Modifier.requiredWidth(screenWidth),
                favoriteAlbum = uiState.favoriteAlbum,
                albumList = uiState.albums,
                onClickFavoriteAlbum = onClickFavoriteAlbum,
                onClickAlbumItem = onClickAlbumItem,
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = "최근 사진",
                textButtonTitle = "모든 사진",
                onClickShowAllAlbum = onClickShowAllPhoto,
            )
        }

        items(
            uiState.recentPhotos,
            key = { photo -> photo.id },
        ) { photo ->
            ArchiveMainPhotoItem(
//                modifier = Modifier.padding(horizontal = 20.dp),
                photo = photo,
                onClickItem = onClickPhotoItem,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ArchiveMainScreenPreview() {
    val dummyPhotos = persistentListOf(
        Photo(id = 1, imageUrl = "https://picsum.photos/seed/photo1/200/300", isFavorite = true),
        Photo(id = 2, imageUrl = "https://picsum.photos/seed/photo2/200/250"),
        Photo(id = 3, imageUrl = "https://picsum.photos/seed/photo3/200/350", isFavorite = true),
        Photo(id = 4, imageUrl = "https://picsum.photos/seed/photo4/200/280"),
        Photo(id = 5, imageUrl = "https://picsum.photos/seed/photo5/200/320"),
        Photo(id = 6, imageUrl = "https://picsum.photos/seed/photo6/200/260", isFavorite = true),
        Photo(id = 7, imageUrl = "https://picsum.photos/seed/photo7/200/290"),
        Photo(id = 8, imageUrl = "https://picsum.photos/seed/photo8/200/310"),
    )

    val dummyAlbums = persistentListOf(
        AlbumPreview(id = 1, title = "제주도 여행 2024", thumbnailUrl = "https://picsum.photos/seed/travel1/200/300", photoCount = 4),
        AlbumPreview(id = 2, title = "가족 생일파티", thumbnailUrl = "https://picsum.photos/seed/family1/200/300", photoCount = 2),
        AlbumPreview(id = 3, title = "대학 동기 모임", thumbnailUrl = "https://picsum.photos/seed/friend1/200/300", photoCount = 3),
    )

    val favoriteAlbum = AlbumPreview(
        id = 0,
        title = "즐겨찾는 사진",
        thumbnailUrl = "https://picsum.photos/seed/fav1/200/300",
        photoCount = 5,
    )

    NekiTheme {
        ArchiveMainScreen(
            uiState = ArchiveMainState(
                favoriteAlbum = favoriteAlbum,
                albums = dummyAlbums,
                recentPhotos = dummyPhotos,
            ),
        )
    }
}
