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
import com.neki.android.core.model.AlbumPreview
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
    navigateToAlbumDetail: (Long, String) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            UploadAlbumSideEffect.NavigateBack -> navigateBack()
            is UploadAlbumSideEffect.NavigateToAlbumDetail -> navigateToAlbumDetail(sideEffect.albumId, sideEffect.title)
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
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                FavoriteAlbumRowComponent(album = uiState.favoriteAlbum)
            }

            items(
                items = uiState.albums,
                key = { album -> album.id },
            ) { album ->
                val isSelected = uiState.selectedAlbums.any { it.id == album.id }
                AlbumRowComponent(
                    album = album,
                    isSelectable = true,
                    isSelected = isSelected,
                    onClick = { onIntent(UploadAlbumIntent.ClickAlbumItem(album)) },
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
    NekiTheme {
        UploadAlbumScreen(
            uiState = UploadAlbumState(
                favoriteAlbum = AlbumPreview(id = 0, title = "즐겨찾기", photoCount = 3),
                albums = persistentListOf(
                    AlbumPreview(id = 1, title = "제주도 여행 2024", photoCount = 4),
                    AlbumPreview(id = 2, title = "가족 생일파티", photoCount = 2),
                    AlbumPreview(id = 3, title = "대학 동기 모임", photoCount = 3),
                    AlbumPreview(id = 4, title = "회사 송년회", photoCount = 5),
                ),
            ),
        )
    }
}

@DevicePreview
@Composable
private fun UploadAlbumScreenSelectingPreview() {
    NekiTheme {
        UploadAlbumScreen(
            uiState = UploadAlbumState(
                favoriteAlbum = AlbumPreview(id = 0, title = "즐겨찾기", photoCount = 3),
                albums = persistentListOf(
                    AlbumPreview(id = 1, title = "제주도 여행 2024", photoCount = 4),
                    AlbumPreview(id = 2, title = "가족 생일파티", photoCount = 2),
                    AlbumPreview(id = 3, title = "대학 동기 모임", photoCount = 3),
                ),
                selectedAlbums = persistentListOf(
                    AlbumPreview(id = 1, title = "제주도 여행 2024", photoCount = 4),
                    AlbumPreview(id = 2, title = "가족 생일파티", photoCount = 2),
                ),
            ),
        )
    }
}
