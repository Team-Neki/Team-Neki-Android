package com.neki.android.feature.photo_upload.impl.qrscan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.collectWithLifecycle
import com.neki.android.feature.photo_upload.impl.PhotoUploadIntent
import com.neki.android.feature.photo_upload.impl.PhotoUploadSideEffect
import com.neki.android.feature.photo_upload.impl.PhotoUploadState
import com.neki.android.feature.photo_upload.impl.PhotoUploadViewModel
import com.neki.android.feature.photo_upload.impl.qrscan.component.QRScanner

@Composable
internal fun QRScanRoute(
    viewModel: PhotoUploadViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPhotoWebView: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PhotoUploadSideEffect.NavigateBack -> navigateBack()
            PhotoUploadSideEffect.NavigateToPhotoWebView -> navigateToPhotoWebView()
        }
    }
    QRScanScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun QRScanScreen(
    uiState: PhotoUploadState = PhotoUploadState(),
    onIntent: (PhotoUploadIntent) -> Unit = {},
) {
    QRScanner(
        modifier = Modifier.fillMaxSize(),
        onQRCodeScanned = { url -> onIntent(PhotoUploadIntent.ScanQRCode(url)) },
    )
}

@Preview(showBackground = true)
@Composable
private fun QRScanScreenPreview() {
    NekiTheme {
        QRScanScreen()
    }
}
