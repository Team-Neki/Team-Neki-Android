package com.neki.android.feature.pose.impl.random

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.CloseTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.pose.impl.random.component.RandomPoseFloatingBarContent
import com.neki.android.feature.pose.impl.random.component.RandomPoseTutorialOverlay

@Composable
internal fun RandomPoseRoute(
    viewModel: RandomPoseViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPoseDetail: (Pose) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            RandomPoseEffect.NavigateBack -> navigateBack()
            is RandomPoseEffect.NavigateToDetail -> navigateToPoseDetail(sideEffect.pose)
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.gray50),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            CloseTitleTopBar(
                title = "랜덤포즈",
                onClose = { onIntent(RandomPoseIntent.ClickCloseIcon) },
            )
            VerticalSpacer(42.dp)
            RandomPoseImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                pose = uiState.currentPose,
            )
            RandomPoseFloatingBarContent(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                isScrapped = uiState.currentPose.isScrapped,
                onClickClose = { onIntent(RandomPoseIntent.ClickCloseIcon) },
                onClickGoToDetail = { onIntent(RandomPoseIntent.ClickGoToDetailIcon) },
                onClickScrap = { onIntent(RandomPoseIntent.ClickScrapIcon) },
            )
        }

        if (uiState.isShowTutorial) {
            RandomPoseTutorialOverlay(
                onClickStart = { onIntent(RandomPoseIntent.ClickStartRandomPose) },
            )
        }
    }
}

@Composable
private fun RandomPoseImage(
    pose: Pose,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = pose.poseImageUrl,
        contentDescription = null,
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.FillWidth,
    )
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
