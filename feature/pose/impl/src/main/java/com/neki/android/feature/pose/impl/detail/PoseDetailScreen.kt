package com.neki.android.feature.pose.impl.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Pose
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.pose.api.PoseResult
import com.neki.android.feature.pose.impl.detail.component.PoseActionBar

@Composable
internal fun PoseDetailRoute(
    viewModel: PoseDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val resultEventBus = LocalResultEventBus.current

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
    )
}

@Composable
internal fun PoseDetailScreen(
    uiState: PoseDetailState = PoseDetailState(),
    onIntent: (PoseDetailIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackTitleTopBar(
            title = "포즈 상세",
            onBack = { onIntent(PoseDetailIntent.ClickBackIcon) },
        )
        AsyncImage(
            model = uiState.pose.poseImageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NekiTheme.colorScheme.gray75,
        )
        PoseActionBar(
            isBookmarked = uiState.pose.isBookmarked,
            onClickBookmark = { onIntent(PoseDetailIntent.ClickBookmarkIcon) },
        )
    }
}

@DevicePreview
@Composable
private fun PoseDetailScreenPreview() {
    NekiTheme {
        PoseDetailScreen(
            uiState = PoseDetailState(
                pose = Pose(
                    id = 1,
                    poseImageUrl = "https://picsum.photos/400/600",
                    isBookmarked = false,
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
                pose = Pose(
                    id = 1,
                    poseImageUrl = "https://picsum.photos/400/600",
                    isBookmarked = true,
                ),
            ),
        )
    }
}
