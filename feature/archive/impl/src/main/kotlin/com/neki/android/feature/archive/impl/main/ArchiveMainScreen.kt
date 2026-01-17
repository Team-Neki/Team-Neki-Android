package com.neki.android.feature.archive.impl.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.archive.impl.ArchiveMainViewModel
import com.neki.android.feature.archive.impl.const.ArchiveConst.ARCHIVE_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.archive.impl.main.component.ArchiveMainAlbumList
import com.neki.android.feature.archive.impl.main.component.ArchiveMainTitleRow
import com.neki.android.feature.archive.impl.main.component.ArchiveMainTopBar
import com.neki.android.feature.archive.impl.main.component.PhotoItem
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ArchiveMainRoute(
    viewModel: ArchiveMainViewModel = hiltViewModel(),
    navigateToQRScan: () -> Unit,
    navigateToGalleryUpload: () -> Unit,
    navigateToAllAlbum: () -> Unit,
    navigateToFavoriteAlbum: (Album) -> Unit,
    navigateToAlbumDetail: (Album) -> Unit,
    navigateToAllPhoto: () -> Unit,
    navigateToPhotoDetail: (Photo) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            ArchiveMainSideEffect.NavigateToQRScan -> navigateToQRScan()
            ArchiveMainSideEffect.NavigateToGalleryUpload -> navigateToGalleryUpload()
            ArchiveMainSideEffect.NavigateToAllAlbum -> navigateToAllAlbum()
            is ArchiveMainSideEffect.NavigateToFavoriteAlbum -> navigateToFavoriteAlbum(sideEffect.album)
            is ArchiveMainSideEffect.NavigateToAlbumDetail -> navigateToAlbumDetail(sideEffect.album)
            ArchiveMainSideEffect.NavigateToAllPhoto -> navigateToAllPhoto()
            is ArchiveMainSideEffect.NavigateToPhotoDetail -> navigateToPhotoDetail(sideEffect.photo)
            is ArchiveMainSideEffect.ShowToastMessage -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    ArchiveMainScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun ArchiveMainScreen(
    uiState: ArchiveMainState = ArchiveMainState(),
    onIntent: (ArchiveMainIntent) -> Unit = {},
) {
    val lazyState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        columns = StaggeredGridCells.Fixed(2),
        state = lazyState,
        contentPadding = PaddingValues(bottom = ARCHIVE_LAYOUT_BOTTOM_PADDING.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTopBar(
                onPlusIconClick = { onIntent(ArchiveMainIntent.ClickAddIcon) },
                onNotificationIconClick = { onIntent(ArchiveMainIntent.ClickNotificationIcon) },
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
                title = "앨범",
                textButtonTitle = "전체 보기",
                onShowAllAlbumClick = { onIntent(ArchiveMainIntent.ClickAllAlbumText) },
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainAlbumList(
                favoriteAlbum = uiState.favoriteAlbum,
                albumList = uiState.albums,
                onFavoriteAlbumClick = { onIntent(ArchiveMainIntent.ClickFavoriteAlbum) },
                onAlbumItemClick = { album -> onIntent(ArchiveMainIntent.ClickAlbumItem(album)) },
            )
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            ArchiveMainTitleRow(
                title = "최근 사진",
                textButtonTitle = "모든 사진",
                onShowAllAlbumClick = { onIntent(ArchiveMainIntent.ClickAllPhotoText) },
            )
        }

        items(uiState.recentPhotos) { photo ->
            PhotoItem(
                photo = photo,
                onItemClick = { onIntent(ArchiveMainIntent.ClickPhotoItem(it)) },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ArchiveMainScreenPreview() {
    val dummyPhotos = persistentListOf(
        Photo(id = 1, imageUrl = "https://picsum.photos/200/300"),
        Photo(id = 2, imageUrl = "https://picsum.photos/200/250"),
        Photo(id = 3, imageUrl = "https://picsum.photos/200/350"),
        Photo(id = 4, imageUrl = "https://picsum.photos/200/280"),
        Photo(id = 5, imageUrl = "https://picsum.photos/200/320"),
        Photo(id = 6, imageUrl = "https://picsum.photos/200/260"),
    )

    val dummyAlbums = persistentListOf(
        Album(id = 1, title = "여행 사진", photoList = dummyPhotos.take(3).let { persistentListOf(*it.toTypedArray()) }),
        Album(id = 2, title = "가족 모임", photoList = dummyPhotos.take(2).let { persistentListOf(*it.toTypedArray()) }),
        Album(id = 3, title = "친구들과", photoList = dummyPhotos.take(4).let { persistentListOf(*it.toTypedArray()) }),
    )

    val favoriteAlbum = Album(
        id = 0,
        title = "즐겨찾는 사진",
        photoList = dummyPhotos.take(5).let { persistentListOf(*it.toTypedArray()) },
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
