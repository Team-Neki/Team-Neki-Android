package com.neki.android.feature.pose.impl

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.core.ui.compose.collectWithLifecycle

@Composable
internal fun PoseDetailRoute(
    viewModel: PoseViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PoseEffect.NavigateBack -> navigateBack()
            is PoseEffect.ShowToast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    PoseDetailScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun PoseDetailScreen(
    uiState: PoseState = PoseState(),
    onIntent: (PoseIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackTitleTopBar(
            title = "포즈 상세",
            onBack = { onIntent(PoseIntent.ClickBackIcon) },
        )
        AsyncImage(
            model = uiState.selectedPose.poseImageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Crop,
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NekiTheme.colorScheme.gray75,
        )
        Icon(
            imageVector = ImageVector.vectorResource(
                if (uiState.selectedPose.isScrapped) R.drawable.ic_scrap_selected
                else R.drawable.icon_scrap_unselected,
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.End)
                .noRippleClickableSingle { onIntent(PoseIntent.ClickScrapIcon) }
                .padding(20.dp),
            tint = NekiTheme.colorScheme.gray500,
        )
    }
}

@DevicePreview
@Composable
private fun PoseDetailScreenPreview() {
    NekiTheme {
        PoseDetailScreen()
    }
}
