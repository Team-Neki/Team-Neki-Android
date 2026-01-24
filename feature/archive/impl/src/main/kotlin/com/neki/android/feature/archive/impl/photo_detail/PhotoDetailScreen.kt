package com.neki.android.feature.archive.impl.photo_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.photo_detail.component.PhotoDetailActionBar
import com.neki.android.feature.archive.impl.util.ImageDownloader

@Composable
internal fun PhotoDetailRoute(
    viewModel: PhotoDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PhotoDetailSideEffect.NavigateBack -> navigateBack()
            is PhotoDetailSideEffect.ShowToastMessage -> {
                nekiToast.showToast(text = sideEffect.message)
            }

            is PhotoDetailSideEffect.DownloadImage -> {
                ImageDownloader.downloadImage(context, sideEffect.imageUrl)
                    .onSuccess {
                        nekiToast.showToast(text = "사진을 갤러리에 다운로드했어요")
                    }
                    .onFailure {
                        nekiToast.showToast(text = "다운로드에 실패했어요")
                    }
            }
        }
    }

    PhotoDetailScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun PhotoDetailScreen(
    uiState: PhotoDetailState = PhotoDetailState(),
    onIntent: (PhotoDetailIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        BackTitleTopBar(
            title = uiState.photo.date,
            onBack = { onIntent(PhotoDetailIntent.ClickBackIcon) },
        )

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            model = uiState.photo.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NekiTheme.colorScheme.gray75,
        )

        PhotoDetailActionBar(
            isFavorite = uiState.photo.isFavorite,
            onDownloadClick = { onIntent(PhotoDetailIntent.ClickDownloadIcon) },
            onFavoriteClick = { onIntent(PhotoDetailIntent.ClickFavoriteIcon) },
            onDeleteClick = { onIntent(PhotoDetailIntent.ClickDeleteIcon) },
        )
    }

    if (uiState.showDeleteDialog) {
        DeletePhotoDialog(
            onDismissRequest = { onIntent(PhotoDetailIntent.DismissDeleteDialog) },
            onDeleteClick = { onIntent(PhotoDetailIntent.ClickDeleteDialogConfirmButton) },
            onCancelClick = { onIntent(PhotoDetailIntent.ClickDeleteDialogCancelButton) },
        )
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }
}

@DevicePreview
@Composable
private fun PhotoDetailScreenPreview() {
    NekiTheme {
        PhotoDetailScreen(
            uiState = PhotoDetailState(
                photo = Photo(
                    id = 1,
                    imageUrl = "https://picsum.photos/400/400",
                    isFavorite = false,
                    date = "2025-12-26",
                ),
            ),
        )
    }
}

@DevicePreview
@Composable
private fun PhotoDetailScreenFavoritePreview() {
    NekiTheme {
        PhotoDetailScreen(
            uiState = PhotoDetailState(
                photo = Photo(
                    id = 1,
                    imageUrl = "https://picsum.photos/400/400",
                    isFavorite = true,
                    date = "2025-12-26",
                ),
            ),
        )
    }
}
