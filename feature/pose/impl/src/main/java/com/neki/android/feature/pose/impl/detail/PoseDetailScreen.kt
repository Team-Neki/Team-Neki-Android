package com.neki.android.feature.pose.impl.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Pose
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.pose.api.PoseResult
import com.neki.android.feature.pose.impl.detail.component.PoseActionBar
import com.neki.android.feature.pose.impl.detail.component.PoseDetailImageItem

@Composable
internal fun PoseDetailRoute(
    viewModel: PoseDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val resultEventBus = LocalResultEventBus.current
    val pagerState = rememberPagerState(initialPage = uiState.currentPage) {
        uiState.poses.size.coerceAtLeast(1)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            viewModel.store.onIntent(PoseDetailIntent.PageChanged(page))
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PoseDetailSideEffect.NavigateBack -> navigateBack()
            is PoseDetailSideEffect.ShowToast -> {
                nekiToast.showToast(sideEffect.message)
            }

            is PoseDetailSideEffect.NotifyBookmarkChanged -> {
                resultEventBus.sendResult(
                    result = PoseResult.BookmarkChanged(sideEffect.poseId, sideEffect.isBookmarked),
                    allowDuplicate = false,
                )
            }
        }
    }

    PoseDetailScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
        pagerState = pagerState,
    )
}

@Composable
internal fun PoseDetailScreen(
    uiState: PoseDetailState = PoseDetailState(),
    onIntent: (PoseDetailIntent) -> Unit = {},
    pagerState: PagerState = rememberPagerState { uiState.poses.size.coerceAtLeast(1) },
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackTitleTopBar(
            title = "포즈 상세",
            onBack = { onIntent(PoseDetailIntent.ClickBackIcon) },
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .clipToBounds(),
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 1,
            ) { page ->
                val index = if (uiState.poses.isEmpty()) 0 else page.coerceIn(0, uiState.poses.lastIndex)
                val pose = uiState.poses.getOrNull(index)

                PoseDetailImageItem(
                    imageUrl = pose?.poseImageUrl,
                )
            }
        }
        PoseActionBar(
            isBookmarked = uiState.pose.isBookmarked,
            onClickBookmark = { onIntent(PoseDetailIntent.ClickBookmarkIcon) },
        )
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }
}

@DevicePreview
@Composable
private fun PoseDetailScreenPreview() {
    NekiTheme {
        PoseDetailScreen(
            uiState = PoseDetailState(
                poses = listOf(
                    Pose(
                        id = 1,
                        poseImageUrl = "https://picsum.photos/400/600",
                        isBookmarked = false,
                    ),
                ),
            ),
        )
    }
}

@DevicePreview
@Composable
private fun PoseDetailScreenBookmarkedPreview() {
    NekiTheme {
        PoseDetailScreen(
            uiState = PoseDetailState(
                poses = listOf(
                    Pose(
                        id = 1,
                        poseImageUrl = "https://picsum.photos/400/600",
                        isBookmarked = true,
                    ),
                ),
            ),
        )
    }
}
