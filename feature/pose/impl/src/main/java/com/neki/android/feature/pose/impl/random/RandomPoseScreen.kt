package com.neki.android.feature.pose.impl.random

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.CloseTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.pose.impl.random.component.RandomPoseFloatingBarContent
import com.neki.android.feature.pose.impl.random.component.RandomPoseImagePager
import com.neki.android.feature.pose.impl.random.component.RandomPoseTutorialOverlay
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun RandomPoseRoute(
    viewModel: RandomPoseViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPoseDetail: (Long) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val pagerState = rememberPagerState { uiState.poseList.size }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            RandomPoseEffect.NavigateBack -> navigateBack()
            is RandomPoseEffect.NavigateToDetail -> navigateToPoseDetail(sideEffect.poseId)
            is RandomPoseEffect.SwipePoseImage -> pagerState.animateScrollToPage(
                page = sideEffect.index,
                animationSpec = tween(durationMillis = 500),
            )

            is RandomPoseEffect.ShowToast -> nekiToast.showToast(sideEffect.message)
        }
    }

    RandomPoseScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
        pagerState = pagerState,
    )
}

@Composable
internal fun RandomPoseScreen(
    uiState: RandomPoseUiState = RandomPoseUiState(),
    onIntent: (RandomPoseIntent) -> Unit = {},
    pagerState: PagerState = rememberPagerState { uiState.poseList.size },
) {
    val hazeState = rememberHazeState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.gray50),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
        ) {
            CloseTitleTopBar(
                title = "랜덤포즈",
                onClose = { onIntent(RandomPoseIntent.ClickCloseIcon) },
            )
            VerticalSpacer(42.dp)

            RandomPoseImagePager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                poseList = uiState.poseList,
                pagerState = pagerState,
                onLeftSwipe = { onIntent(RandomPoseIntent.ClickLeftSwipe) },
                onRightSwipe = { onIntent(RandomPoseIntent.ClickRightSwipe) },
            )

            uiState.currentPose?.let { pose ->
                RandomPoseFloatingBarContent(
                    modifier = Modifier.fillMaxWidth(),
                    isScrapped = pose.isScrapped,
                    onClickClose = { onIntent(RandomPoseIntent.ClickCloseIcon) },
                    onClickGoToDetail = { onIntent(RandomPoseIntent.ClickGoToDetailIcon) },
                    onClickScrap = { onIntent(RandomPoseIntent.ClickScrapIcon) },
                )
            }
        }

        if (uiState.isShowTutorial) {
            RandomPoseTutorialOverlay(
                hazeState = hazeState,
                onClickStart = { onIntent(RandomPoseIntent.ClickStartRandomPose) },
            )
        }
    }
}

@DevicePreview
@Composable
private fun RandomPoseScreenPreview() {
    NekiTheme {
        RandomPoseScreen(
            uiState = RandomPoseUiState(isShowTutorial = false),
        )
    }
}

@DevicePreview
@Composable
private fun RandomPoseScreenTutorialPreview() {
    NekiTheme {
        RandomPoseScreen(
            uiState = RandomPoseUiState(isShowTutorial = true),
        )
    }
}
