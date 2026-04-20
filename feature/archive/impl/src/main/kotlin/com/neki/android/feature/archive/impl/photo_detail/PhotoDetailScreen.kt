package com.neki.android.feature.archive.impl.photo_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.topbar.BackTitleOptionTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.ui.component.DropdownPopup
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.api.PhotoDetailResult
import com.neki.android.feature.archive.impl.component.DeletePhotoDialog
import com.neki.android.feature.archive.impl.photo_detail.component.MemoTextField
import com.neki.android.feature.archive.impl.photo_detail.component.PhotoDetailActionBar
import com.neki.android.feature.archive.impl.photo_detail.component.PhotoDetailImageItem
import com.neki.android.feature.archive.impl.util.ImageDownloader
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
internal fun PhotoDetailRoute(
    viewModel: PhotoDetailViewModel,
    navigateBack: () -> Unit,
    navigateToSelectAlbum: (Long) -> Unit,
    navigateToAlbumDetail: (Long, String) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val resultEventBus = LocalResultEventBus.current
    val pagerState = rememberPagerState(initialPage = uiState.currentPage) {
        uiState.photos.size.coerceAtLeast(1)
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            viewModel.store.onIntent(PhotoDetailIntent.PageChanged(page))
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .filter { it }
            .collect {
                viewModel.store.onIntent(PhotoDetailIntent.PageScrollStarted)
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

            is PhotoDetailSideEffect.NavigateToSelectAlbum -> navigateToSelectAlbum(sideEffect.photoId)
            is PhotoDetailSideEffect.ShowActionToast -> nekiToast.showActionToast(
                text = "사진을 앨범에 추가했어요",
                buttonText = "앨범으로",
                onClickActionButton = { navigateToAlbumDetail(sideEffect.albumId, sideEffect.albumTitle) },
            )
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
    pagerState: PagerState = rememberPagerState { uiState.photos.size.coerceAtLeast(1) },
) {
    val density = LocalDensity.current
    var actionBarHeightDp by remember { mutableStateOf(0.dp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        BackTitleOptionTopBar(
            title = uiState.photo.date,
            onBack = { onIntent(PhotoDetailIntent.ClickBackIcon) },
            onClickIcon = { onIntent(PhotoDetailIntent.ClickOptionIcon) },
        )

        PhotoDetailPagerArea(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clipToBounds(),
            uiState = uiState,
            actionBarHeightDp = actionBarHeightDp,
            pagerState = pagerState,
            onIntent = onIntent,
        )

        if (uiState.memoMode != MemoMode.Editing) {
            PhotoDetailActionBar(
                modifier = Modifier.onSizeChanged { size ->
                    actionBarHeightDp = with(density) { size.height.toDp() }
                },
                isFavorite = uiState.photo.isFavorite,
                onClickDownload = { onIntent(PhotoDetailIntent.ClickDownloadIcon) },
                onClickFavorite = { onIntent(PhotoDetailIntent.ClickFavoriteIcon) },
                onClickMemo = { onIntent(PhotoDetailIntent.ClickMemoIcon) },
                onClickDelete = { onIntent(PhotoDetailIntent.ClickDeleteIcon) },
            )
        }
    }

    if (uiState.isShowOptionPopup) {
        val density = LocalDensity.current
        val popupOffsetX = with(density) { (-20).dp.toPx().toInt() }
        val popupOffsetY = with(density) { 54.dp.toPx().toInt() }

        DropdownPopup(
            items = persistentListOf("앨범에 추가"),
            onSelect = { onIntent(PhotoDetailIntent.ClickAddToAlbumOption) },
            onDismissRequest = { onIntent(PhotoDetailIntent.DismissOptionPopup) },
            itemLabel = { it },
            modifier = Modifier.width(120.dp),
            offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
            alignment = Alignment.TopEnd,
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

@Composable
private fun PhotoDetailPagerArea(
    modifier: Modifier,
    uiState: PhotoDetailState,
    actionBarHeightDp: Dp,
    pagerState: PagerState,
    onIntent: (PhotoDetailIntent) -> Unit,
) {
    val isMemoActive = uiState.memoMode == MemoMode.Expanded ||
        uiState.memoMode == MemoMode.Editing

    Box(modifier = modifier) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (uiState.memoMode == MemoMode.Editing) actionBarHeightDp else 0.dp),
            state = pagerState,
            beyondViewportPageCount = 1,
            userScrollEnabled = !isMemoActive,
        ) { page ->
            val index = if (uiState.photos.isEmpty()) 0 else page.coerceIn(0, uiState.photos.lastIndex)
            val photo = uiState.photos.getOrNull(index)

            PhotoDetailImageItem(
                imageUrl = photo?.imageUrl,
                isScrollInProgress = pagerState.isScrollInProgress,
                isTapEnabled = !isMemoActive,
                onClickLeft = { onIntent(PhotoDetailIntent.ClickLeftPhoto) },
                onClickRight = { onIntent(PhotoDetailIntent.ClickRightPhoto) },
            )
        }

        AnimatedVisibility(
            visible = isMemoActive,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80202227))
                    .noRippleClickableSingle { onIntent(PhotoDetailIntent.ClickMemoFold) },
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .then(
                    if (uiState.memoMode == MemoMode.Editing) Modifier.imePadding()
                    else Modifier.windowInsetsPadding(
                        WindowInsets.ime.exclude(WindowInsets(bottom = actionBarHeightDp)),
                    ),
                ),
            visible = uiState.memoMode != MemoMode.Closed,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            MemoTextField(
                memo = uiState.memo,
                memoMode = uiState.memoMode,
                onClickMemoMore = { onIntent(PhotoDetailIntent.ClickMemoMore) },
                onClickMemoText = { onIntent(PhotoDetailIntent.ClickMemoText) },
                onClickMemoFold = { onIntent(PhotoDetailIntent.ClickMemoFold) },
                onClickMemoCancel = { onIntent(PhotoDetailIntent.ClickMemoCancel) },
                onClickMemoDone = { onIntent(PhotoDetailIntent.ClickMemoDone(it)) },
                onMemoTextChanged = { onIntent(PhotoDetailIntent.MemoTextChanged(it)) },
            )
        }
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
