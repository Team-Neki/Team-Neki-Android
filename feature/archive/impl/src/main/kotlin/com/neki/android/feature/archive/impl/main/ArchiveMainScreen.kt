package com.neki.android.feature.archive.impl.main

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
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.neki.android.feature.archive.api.ArchiveNavKey
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.component.AddAlbumBottomSheet
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_GRID_ITEM_SPACING
import com.neki.android.feature.archive.impl.const.ArchiveConst.PHOTO_GRAY_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.main.component.ArchiveMainAlbumList
import com.neki.android.feature.archive.impl.main.component.ArchiveMainPhotoItem
import com.neki.android.feature.archive.impl.main.component.ArchiveMainTitleRow
import com.neki.android.feature.archive.impl.main.component.ArchiveMainTopBar
import com.neki.android.feature.archive.impl.component.EmptyPhotoContent
import com.neki.android.feature.archive.impl.main.component.GotoTopButton
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ArchiveMainRoute(
    viewModel: ArchiveMainViewModel = hiltViewModel(),
    navigateToQRScan: () -> Unit,
    navigateToAllAlbum: () -> Unit,
    navigateToFavoriteAlbum: (Long) -> Unit,
    navigateToAlbumDetail: (Long, String) -> Unit,
    navigateToAllPhoto: () -> Unit,
    navigateToPhotoDetail: (ArchiveNavKey.PhotoDetail) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lazyState = rememberLazyStaggeredGridState()
    val nekiToast = remember { NekiToast(context) }

    LaunchedEffect(Unit) {
        viewModel.logArchivingView()
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            ArchiveMainSideEffect.NavigateToQRScan -> navigateToQRScan()
            ArchiveMainSideEffect.NavigateToAllAlbum -> navigateToAllAlbum()
            is ArchiveMainSideEffect.NavigateToFavoriteAlbum -> navigateToFavoriteAlbum(sideEffect.albumId)
            is ArchiveMainSideEffect.NavigateToAlbumDetail -> navigateToAlbumDetail(sideEffect.albumId, sideEffect.title)
            ArchiveMainSideEffect.NavigateToAllPhoto -> navigateToAllPhoto()
            is ArchiveMainSideEffect.NavigateToPhotoDetail -> navigateToPhotoDetail(
                ArchiveNavKey.PhotoDetail(photos = sideEffect.photos, initialIndex = sideEffect.index),
            )

            ArchiveMainSideEffect.ScrollToTop -> lazyState.animateScrollToItem(0)
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
                showTooltip = uiState.isFirstEntered,
                onClickQRCodeIcon = { onIntent(ArchiveMainIntent.ClickQRScanIcon) },
                onDismissToolTipPopup = { onIntent(ArchiveMainIntent.DismissToolTipPopup) },
            )
            ArchiveMainContent(
                uiState = uiState,
                lazyState = lazyState,
                onClickShowAllAlbum = { onIntent(ArchiveMainIntent.ClickAllAlbumText) },
                onClickFavoriteAlbum = { onIntent(ArchiveMainIntent.ClickFavoriteAlbum) },
                onClickAlbumItem = { onIntent(ArchiveMainIntent.ClickAlbumItem(it.id, it.title)) },
                onClickShowAllPhoto = { onIntent(ArchiveMainIntent.ClickAllPhotoText) },
                onClickPhotoItem = { photo, index -> onIntent(ArchiveMainIntent.ClickPhotoItem(photo, index)) },
                onClickFavorite = { photo -> onIntent(ArchiveMainIntent.ClickFavoriteIcon(photo)) },
                onClickAddAlbum = { onIntent(ArchiveMainIntent.ClickAddAlbum) },
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
        val existingAlbumNames = remember(uiState.albums) { uiState.albums.map { it.title } }

        val errorMessage by remember(uiState.albumNameTextState.text) {
            derivedStateOf {
                val name = uiState.albumNameTextState.text.toString()
                when {
                    existingAlbumNames.contains(name) -> "이미 사용 중인 앨범명이에요."
                    else -> null
                }
            }
        }

        AddAlbumBottomSheet(
            textFieldState = uiState.albumNameTextState,
            onDismissRequest = { onIntent(ArchiveMainIntent.DismissAddAlbumBottomSheet) },
            onClickCancel = { onIntent(ArchiveMainIntent.DismissAddAlbumBottomSheet) },
            onClickConfirm = { onIntent(ArchiveMainIntent.ClickAddAlbumButton) },
            isError = errorMessage != null,
            errorMessage = errorMessage,
        )
    }
}

@Composable
private fun ArchiveMainContent(
    uiState: ArchiveMainState,
    lazyState: LazyStaggeredGridState,
    onClickShowAllAlbum: () -> Unit,
    onClickFavoriteAlbum: () -> Unit,
    onClickAlbumItem: (AlbumPreview) -> Unit,
    onClickShowAllPhoto: () -> Unit,
    onClickPhotoItem: (Photo, Int) -> Unit,
    onClickFavorite: (Photo) -> Unit,
    onClickAddAlbum: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }

    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxWidth(),
        columns = StaggeredGridCells.Fixed(2),
        state = lazyState,
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            bottom = PHOTO_GRAY_LAYOUT_BOTTOM_PADDING.dp,
        ),
        verticalItemSpacing = ARCHIVE_GRID_ITEM_SPACING.dp,
        horizontalArrangement = Arrangement.spacedBy(ARCHIVE_GRID_ITEM_SPACING.dp),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
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
                onClickAddAlbum = onClickAddAlbum,
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
                modifier = Modifier.padding(top = 12.dp),
                title = "최근 사진",
                textButtonTitle = "모든 사진",
                onClickShowAllAlbum = onClickShowAllPhoto,
            )
        }

        if (uiState.recentPhotos.isEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                EmptyPhotoContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 70.dp),
                    emptyText = "아직 등록된 사진이 없어요\n찍은 네컷을 저장해보세요!",
                )
            }
        }

        items(
            count = uiState.recentPhotos.size,
            key = { index -> uiState.recentPhotos[index].id },
        ) { index ->
            val photo = uiState.recentPhotos[index]
            ArchiveMainPhotoItem(
                photo = photo,
                onClickItem = { onClickPhotoItem(it, index) },
                onClickFavorite = onClickFavorite,
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
        title = "즐겨찾기",
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

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ArchiveMainScreenEmptyPreview() {
    val favoriteAlbum = AlbumPreview(
        id = 0,
        title = "즐겨찾기",
    )

    NekiTheme {
        ArchiveMainScreen(
            uiState = ArchiveMainState(
                favoriteAlbum = favoriteAlbum,
            ),
        )
    }
}
