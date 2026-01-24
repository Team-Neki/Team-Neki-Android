package com.neki.android.feature.pose.impl.random

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PoseIntent
import com.neki.android.core.model.PoseState
import com.neki.android.feature.pose.impl.main.PoseViewModel

@Composable
internal fun RandomPoseRoute(
    viewModel: PoseViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPoseDetail: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()


}

@Composable
internal fun RandomPoseScreen(
    uiState: PoseState = PoseState(),
    onIntent: (PoseIntent) -> Unit = {},
) {

}

@ComponentPreview
@Composable
private fun RandomPoseScreenPreview() {
    NekiTheme {
        RandomPoseScreen()
    }
}
