package com.neki.android.feature.photo_upload.impl.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.AlbumRowComponent
import com.neki.android.core.ui.component.FavoriteAlbumRowComponent
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.photo_upload.impl.album.component.UploadAlbumTopBar
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun UploadAlbumRoute(
    viewModel: UploadAlbumViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToAlbumDetail: (Long) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            UploadAlbumSideEffect.NavigateBack -> navigateBack()
            is UploadAlbumSideEffect.NavigateToAlbumDetail -> navigateToAlbumDetail(sideEffect.albumId)
            is UploadAlbumSideEffect.ShowToastMessage -> nekiToast.showToast(sideEffect.message)
        }
    }

    UploadAlbumScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UploadAlbumScreen(
    uiState: UploadAlbumState = UploadAlbumState(),
    onIntent: (UploadAlbumIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        UploadAlbumTopBar(
            count = uiState.count,
            onClickBack = { onIntent(UploadAlbumIntent.ClickBackIcon) },
            onClickUpload = { onIntent(UploadAlbumIntent.ClickUploadButton) },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                FavoriteAlbumRowComponent(album = uiState.favoriteAlbum)
            }

            items(
                items = uiState.albums,
                key = { album -> album.id },
            ) { album ->
                val isSelected = uiState.selectedAlbumIds.any { it == album.id }
                AlbumRowComponent(
                    album = album,
                    isSelectable = true,
                    isSelected = isSelected,
                    onClick = { onIntent(UploadAlbumIntent.ClickAlbumItem(album.id)) },
                )
            }
        }
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }
}

@DevicePreview
@Composable
private fun UploadAlbumScreenPreview() {
    val travelPhotos = persistentListOf(
        Photo(id = 101, imageUrl = "https://picsum.photos/seed/album_travel1/200/300"),
        Photo(id = 102, imageUrl = "https://picsum.photos/seed/album_travel2/200/280"),
        Photo(id = 103, imageUrl = "https://picsum.photos/seed/album_travel3/200/320"),
        Photo(id = 104, imageUrl = "https://picsum.photos/seed/album_travel4/200/260"),
    )

    val familyPhotos = persistentListOf(
        Photo(id = 201, imageUrl = "https://picsum.photos/seed/album_family1/200/300"),
        Photo(id = 202, imageUrl = "https://picsum.photos/seed/album_family2/200/290"),
    )

    val friendPhotos = persistentListOf(
        Photo(id = 301, imageUrl = "https://picsum.photos/seed/album_friend1/200/300"),
        Photo(id = 302, imageUrl = "https://picsum.photos/seed/album_friend2/200/310"),
        Photo(id = 303, imageUrl = "https://picsum.photos/seed/album_friend3/200/280"),
    )

    val partyPhotos = persistentListOf(
        Photo(id = 401, imageUrl = "https://picsum.photos/seed/album_party1/200/300"),
        Photo(id = 402, imageUrl = "https://picsum.photos/seed/album_party2/200/320"),
        Photo(id = 403, imageUrl = "https://picsum.photos/seed/album_party3/200/280"),
        Photo(id = 404, imageUrl = "https://picsum.photos/seed/album_party4/200/290"),
        Photo(id = 405, imageUrl = "https://picsum.photos/seed/album_party5/200/310"),
    )

    val favoritePhotos = persistentListOf(
        Photo(id = 501, imageUrl = "https://picsum.photos/seed/album_fav1/200/300"),
        Photo(id = 502, imageUrl = "https://picsum.photos/seed/album_fav2/200/280"),
        Photo(id = 503, imageUrl = "https://picsum.photos/seed/album_fav3/200/320"),
    )

    val dummyAlbums = persistentListOf(
        Album(id = 1, title = "제주도 여행 2024", photoList = travelPhotos),
        Album(id = 2, title = "가족 생일파티", photoList = familyPhotos),
        Album(id = 3, title = "대학 동기 모임", photoList = friendPhotos),
        Album(id = 4, title = "회사 송년회", photoList = partyPhotos),
    )

    NekiTheme {
        UploadAlbumScreen(
            uiState = UploadAlbumState(
                favoriteAlbum = Album(id = 0, title = "즐겨찾는 사진", photoList = favoritePhotos),
                albums = dummyAlbums,
            ),
        )
    }
}

@DevicePreview
@Composable
private fun UploadAlbumScreenSelectingPreview() {
    val travelPhotos = persistentListOf(
        Photo(id = 101, imageUrl = "https://picsum.photos/seed/sel_travel1/200/300"),
        Photo(id = 102, imageUrl = "https://picsum.photos/seed/sel_travel2/200/280"),
        Photo(id = 103, imageUrl = "https://picsum.photos/seed/sel_travel3/200/320"),
    )

    val familyPhotos = persistentListOf(
        Photo(id = 201, imageUrl = "https://picsum.photos/seed/sel_family1/200/300"),
        Photo(id = 202, imageUrl = "https://picsum.photos/seed/sel_family2/200/290"),
    )

    val friendPhotos = persistentListOf(
        Photo(id = 301, imageUrl = "https://picsum.photos/seed/sel_friend1/200/300"),
        Photo(id = 302, imageUrl = "https://picsum.photos/seed/sel_friend2/200/310"),
    )

    val favoritePhotos = persistentListOf(
        Photo(id = 501, imageUrl = "https://picsum.photos/seed/sel_fav1/200/300"),
        Photo(id = 502, imageUrl = "https://picsum.photos/seed/sel_fav2/200/280"),
    )

    val dummyAlbums = persistentListOf(
        Album(id = 1, title = "제주도 여행 2024", photoList = travelPhotos),
        Album(id = 2, title = "가족 생일파티", photoList = familyPhotos),
        Album(id = 3, title = "대학 동기 모임", photoList = friendPhotos),
    )

    NekiTheme {
        UploadAlbumScreen(
            uiState = UploadAlbumState(
                favoriteAlbum = Album(id = 0, title = "즐겨찾는 사진", photoList = favoritePhotos),
                albums = dummyAlbums,
                selectedAlbumIds = persistentListOf(),
            ),
        )
    }
}
