package com.neki.android.feature.archive.impl.photo_detail

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.api.PhotoDetailResult
import com.neki.android.feature.archive.impl.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.photo_detail.component.PhotoDetailActionBar
import com.neki.android.feature.archive.impl.util.ImageDownloader
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
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
    val pagerState = rememberPagerState(initialPage = uiState.currentPage) { Int.MAX_VALUE }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            viewModel.store.onIntent(PhotoDetailIntent.PageChanged(page))
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PhotoDetailSideEffect.NavigateBack -> navigateBack()
            PhotoDetailSideEffect.NotifyPhotoUpdated -> resultEventBus.sendResult(result = PhotoDetailResult, allowDuplicate = false)
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

            is PhotoDetailSideEffect.AnimateToPage -> coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = sideEffect.index,
                    animationSpec = spring(),
                )
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
    pagerState: PagerState = rememberPagerState { Int.MAX_VALUE },
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
                .weight(1f)
                .clipToBounds(),
            state = pagerState,
            beyondViewportPageCount = 1,
        ) { page ->
            val index = if (uiState.photos.isEmpty()) 0 else page % uiState.photos.size
            val zoomState = rememberZoomState()
            var contentWidth by remember { mutableIntStateOf(0) }
            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { contentWidth = it.width }
                        .zoomable(
                            zoomState = zoomState,
                            scrollGesturePropagation = ScrollGesturePropagation.ContentEdge,
                            onTap = { position: Offset ->
                                if (!pagerState.isScrollInProgress && contentWidth > 0 && zoomState.scale <= 1f) {
                                    if (position.x < contentWidth / 2) {
                                        onIntent(PhotoDetailIntent.ClickLeftPhoto)
                                    } else {
                                        onIntent(PhotoDetailIntent.ClickRightPhoto)
                                    }
                                }
                            },
                        ),
                    model = uiState.photos.getOrNull(index)?.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    onSuccess = { state -> zoomState.setContentSize(state.painter.intrinsicSize) },
                )
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
