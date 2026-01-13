package com.neki.android.feature.pose.impl

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle

@Composable
internal fun PoseRoue(
    viewModel: PoseViewModel = hiltViewModel(),
    navigateToPoseDetail: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PoseEffect.NavigateToPoseDetail -> navigateToPoseDetail()
            is PoseEffect.ShowToast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    PoseScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun PoseScreen(
    uiState: PoseState = PoseState(),
    onIntent: (PoseIntent) -> Unit = {},
) {

}

@DevicePreview
@Composable
private fun PoseScreenPreview() {
    NekiTheme {
        PoseScreen()
    }
}
