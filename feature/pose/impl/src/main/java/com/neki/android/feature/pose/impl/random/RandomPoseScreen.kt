package com.neki.android.feature.pose.impl.random

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.topbar.CloseTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.pose.impl.random.component.RandomPoseFloatingBarContent
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
    val imageLoader = remember { ImageLoader(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            RandomPoseEffect.NavigateBack -> navigateBack()
            is RandomPoseEffect.NavigateToDetail -> navigateToPoseDetail(sideEffect.poseId)
            is RandomPoseEffect.ShowToast -> nekiToast.showToast(sideEffect.message)
            is RandomPoseEffect.RequestImageBuilder -> {
                val request = ImageRequest.Builder(context)
                    .data(sideEffect.imageUrl)
                    .build()
                imageLoader.execute(request)
            }
        }
    }

    RandomPoseScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun RandomPoseScreen(
    uiState: RandomPoseUiState = RandomPoseUiState(),
    onIntent: (RandomPoseIntent) -> Unit = {},
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
            uiState.currentPose?.let { pose ->
                RandomPoseImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    pose = pose,
                    onLeftSwipe = { onIntent(RandomPoseIntent.ClickLeftSwipe) },
                    onRightSwipe = { onIntent(RandomPoseIntent.ClickRightSwipe) },
                )
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

@Composable
private fun RandomPoseImage(
    pose: Pose,
    modifier: Modifier = Modifier,
    onLeftSwipe: () -> Unit = {},
    onRightSwipe: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(horizontal = 10.dp),
    ) {
        AsyncImage(
            model = pose.poseImageUrl,
            contentDescription = null,
            modifier = modifier
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.FillWidth,
        )
        Row(
            modifier = Modifier.matchParentSize(),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .noRippleClickableSingle(onClick = onLeftSwipe),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .noRippleClickableSingle(onClick = onRightSwipe),
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
