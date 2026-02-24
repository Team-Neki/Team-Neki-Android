package com.neki.android.feature.archive.impl.photo_detail

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.photo_detail.component.PhotoDetailActionBar
import com.neki.android.feature.archive.impl.util.ImageDownloader
import timber.log.Timber

@Composable
internal fun PhotoDetailRoute(
    viewModel: PhotoDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val resultEventBus = LocalResultEventBus.current
    val pagerState = rememberPagerState(initialPage = uiState.currentIndex) { uiState.photos.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            viewModel.store.onIntent(PhotoDetailIntent.PageChanged(page))
        }
    }

    LaunchedEffect(uiState.currentIndex) {
        if (pagerState.currentPage != uiState.currentIndex) {
            pagerState.animateScrollToPage(
                page = uiState.currentIndex,
                animationSpec = tween(durationMillis = 300),
            )
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PhotoDetailSideEffect.NavigateBack -> navigateBack()
            is PhotoDetailSideEffect.NotifyPhotoUpdated -> resultEventBus.sendResult(result = sideEffect.result, allowDuplicate = false)
            is PhotoDetailSideEffect.ShowToastMessage -> nekiToast.showToast(text = sideEffect.message)
            is PhotoDetailSideEffect.DownloadImage -> {
                ImageDownloader.downloadImage(context, sideEffect.imageUrl)
                    .onSuccess {
                        nekiToast.showToast(text = "사진을 갤러리에 다운로드했어요")
                    }
                    .onFailure { e ->
                        nekiToast.showToast(text = "다운로드에 실패했어요")
                        Timber.e(e)
                    }
            }
        }
    }

    PhotoDetailScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
        pagerState = pagerState,
    )
}

@Composable
internal fun PhotoDetailScreen(
    uiState: PhotoDetailState = PhotoDetailState(),
    onIntent: (PhotoDetailIntent) -> Unit = {},
    pagerState: PagerState = rememberPagerState { uiState.photos.size },
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

        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState,
        ) { index ->
            Box {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = uiState.photos.getOrNull(index)?.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
                Row(modifier = Modifier.matchParentSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .noRippleClickableSingle {
                                if (!pagerState.isScrollInProgress && pagerState.currentPage > 0) {
                                    onIntent(PhotoDetailIntent.PageChanged(pagerState.currentPage - 1))
                                }
                            },
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .noRippleClickableSingle {
                                if (!pagerState.isScrollInProgress && pagerState.currentPage < uiState.photos.lastIndex) {
                                    onIntent(PhotoDetailIntent.PageChanged(pagerState.currentPage + 1))
                                }
                            },
                    )
                }
            }
        }

        PhotoDetailActionBar(
            isFavorite = uiState.photo.isFavorite,
            onClickDownload = { onIntent(PhotoDetailIntent.ClickDownloadIcon) },
            onClickFavorite = { onIntent(PhotoDetailIntent.ClickFavoriteIcon) },
            onClickDelete = { onIntent(PhotoDetailIntent.ClickDeleteIcon) },
        )
    }

    if (uiState.isShowDeleteDialog) {
        DeletePhotoDialog(
            onDismissRequest = { onIntent(PhotoDetailIntent.DismissDeleteDialog) },
            onClickDelete = { onIntent(PhotoDetailIntent.ClickDeleteDialogConfirmButton) },
            onClickCancel = { onIntent(PhotoDetailIntent.ClickDeleteDialogCancelButton) },
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
                photos = listOf(
                    Photo(
                        id = 1,
                        imageUrl = "https://picsum.photos/400/400",
                        isFavorite = false,
                        date = "2025-12-26",
                    ),
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
                photos = listOf(
                    Photo(
                        id = 1,
                        imageUrl = "https://picsum.photos/400/400",
                        isFavorite = true,
                        date = "2025-12-26",
                    ),
                ),
            ),
        )
    }
}
