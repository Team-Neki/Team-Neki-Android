package com.neki.android.feature.archive.impl.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.component.AddAlbumBottomSheet
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_LAYOUT_HORIZONTAL_PADDING
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
    navigateToGalleryUpload: (List<String>) -> Unit,
    navigateToAllAlbum: () -> Unit,
    navigateToFavoriteAlbum: (Album) -> Unit,
    navigateToAlbumDetail: (Album) -> Unit,
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
            is ArchiveMainSideEffect.NavigateToGalleryUpload -> navigateToGalleryUpload(sideEffect.uriStrings)
            ArchiveMainSideEffect.NavigateToAllAlbum -> navigateToAllAlbum()
            is ArchiveMainSideEffect.NavigateToFavoriteAlbum -> navigateToFavoriteAlbum(sideEffect.album)
            is ArchiveMainSideEffect.NavigateToAlbumDetail -> navigateToAlbumDetail(sideEffect.album)
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
        ArchiveMainContent(
            uiState = uiState,
            lazyState = lazyState,
            onPlusIconClick = { onIntent(ArchiveMainIntent.ClickAddIcon) },
            onNotificationIconClick = { onIntent(ArchiveMainIntent.ClickNotificationIcon) },
            onQRScanClick = { onIntent(ArchiveMainIntent.ClickQRScanRow) },
            onGalleryClick = { onIntent(ArchiveMainIntent.ClickGalleryUploadRow) },
            onNewAlbumClick = { onIntent(ArchiveMainIntent.ClickAddNewAlbumRow) },
            onDismissPopup = { onIntent(ArchiveMainIntent.DismissAddDialog) },
            onShowAllAlbumClick = { onIntent(ArchiveMainIntent.ClickAllAlbumText) },
            onFavoriteAlbumClick = { onIntent(ArchiveMainIntent.ClickFavoriteAlbum) },
            onAlbumItemClick = { album -> onIntent(ArchiveMainIntent.ClickAlbumItem(album)) },
            onShowAllPhotoClick = { onIntent(ArchiveMainIntent.ClickAllPhotoText) },
            onPhotoItemClick = { photo -> onIntent(ArchiveMainIntent.ClickPhotoItem(photo)) },
        )

        GotoTopButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            visible = !lazyState.isScrollInProgress && lazyState.firstVisibleItemIndex != 0,
            onClick = { onIntent(ArchiveMainIntent.ClickGoToTopButton) },
        )
    }

    if (uiState.showAddAlbumBottomSheet) {
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
            onCancelClick = { onIntent(ArchiveMainIntent.DismissAddAlbumBottomSheet) },
            onConfirmClick = {
                val albumName = textFieldState.text.toString()
                if (errorMessage == null && albumName.isNotBlank()) {
                    onIntent(ArchiveMainIntent.ClickAddAlbumButton(albumName))
                }
            },
            isError = errorMessage != null,
            errorMessage = errorMessage,
        )
    }

    if (uiState.showChooseWithAlbumDialog) {
        ChooseWithAlbumDialog(
            onDismissRequest = { onIntent(ArchiveMainIntent.DismissChooseWithAlbumDialog) },
            onUploadWithOutAlbumClick = { onIntent(ArchiveMainIntent.ClickUploadWithoutAlbumRow) },
            onUploadWithAlbumClick = { onIntent(ArchiveMainIntent.ClickUploadWithAlbumRow) },
        )
    }
}

@Composable
private fun ArchiveMainContent(
    uiState: ArchiveMainState,
    lazyState: LazyStaggeredGridState,
    onPlusIconClick: () -> Unit,
    onNotificationIconClick: () -> Unit,
    onQRScanClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onNewAlbumClick: () -> Unit,
    onDismissPopup: () -> Unit,
    onShowAllAlbumClick: () -> Unit,
    onFavoriteAlbumClick: () -> Unit,
    onAlbumItemClick: (Album) -> Unit,
    onShowAllPhotoClick: () -> Unit,
    onPhotoItemClick: (Photo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxWidth(),
        columns = StaggeredGridCells.Fixed(2),
        state = lazyState,
        contentPadding = PaddingValues(
            start = ARCHIVE_LAYOUT_HORIZONTAL_PADDING.dp,
            end = ARCHIVE_LAYOUT_HORIZONTAL_PADDING.dp,
            bottom = ARCHIVE_LAYOUT_BOTTOM_PADDING.dp,
        ),
        verticalItemSpacing = ARCHIVE_GRID_ITEM_SPACING.dp,
        horizontalArrangement = Arrangement.spacedBy(ARCHIVE_GRID_ITEM_SPACING.dp),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTopBar(
                showTooltip = uiState.isFirstEntered,
                showAddPopup = uiState.showAddDialog,
                onPlusIconClick = onPlusIconClick,
                onNotificationIconClick = onNotificationIconClick,
                onQRScanClick = onQRScanClick,
                onGalleryClick = onGalleryClick,
                onNewAlbumClick = onNewAlbumClick,
                onDismissPopup = onDismissPopup,
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
                title = "앨범",
                textButtonTitle = "전체 보기",
                onShowAllAlbumClick = onShowAllAlbumClick,
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainAlbumList(
                modifier = Modifier.requiredWidth(screenWidth),
                favoriteAlbum = uiState.favoriteAlbum,
                albumList = uiState.albums,
                onFavoriteAlbumClick = onFavoriteAlbumClick,
                onAlbumItemClick = onAlbumItemClick,
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
                title = "최근 사진",
                textButtonTitle = "모든 사진",
                onShowAllAlbumClick = onShowAllPhotoClick,
            )
        }

        items(
            uiState.recentPhotos,
            key = { photo -> photo.id },
        ) { photo ->
            ArchiveMainPhotoItem(
                photo = photo,
                onItemClick = onPhotoItemClick,
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

    val travelPhotos = persistentListOf(
        Photo(id = 101, imageUrl = "https://picsum.photos/seed/travel1/200/300"),
        Photo(id = 102, imageUrl = "https://picsum.photos/seed/travel2/200/280"),
        Photo(id = 103, imageUrl = "https://picsum.photos/seed/travel3/200/320"),
        Photo(id = 104, imageUrl = "https://picsum.photos/seed/travel4/200/260"),
    )

    val familyPhotos = persistentListOf(
        Photo(id = 201, imageUrl = "https://picsum.photos/seed/family1/200/300"),
        Photo(id = 202, imageUrl = "https://picsum.photos/seed/family2/200/290"),
    )

    val friendPhotos = persistentListOf(
        Photo(id = 301, imageUrl = "https://picsum.photos/seed/friend1/200/300"),
        Photo(id = 302, imageUrl = "https://picsum.photos/seed/friend2/200/310"),
        Photo(id = 303, imageUrl = "https://picsum.photos/seed/friend3/200/280"),
    )

    val dummyAlbums = persistentListOf(
        Album(id = 1, title = "제주도 여행 2024", photoList = travelPhotos),
        Album(id = 2, title = "가족 생일파티", photoList = familyPhotos),
        Album(id = 3, title = "대학 동기 모임", photoList = friendPhotos),
    )

    val favoritePhotos = persistentListOf(
        Photo(id = 401, imageUrl = "https://picsum.photos/seed/fav1/200/300"),
        Photo(id = 402, imageUrl = "https://picsum.photos/seed/fav2/200/280"),
        Photo(id = 403, imageUrl = "https://picsum.photos/seed/fav3/200/320"),
        Photo(id = 404, imageUrl = "https://picsum.photos/seed/fav4/200/290"),
        Photo(id = 405, imageUrl = "https://picsum.photos/seed/fav5/200/310"),
    )

    val favoriteAlbum = Album(
        id = 0,
        title = "즐겨찾는 사진",
        photoList = favoritePhotos,
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
