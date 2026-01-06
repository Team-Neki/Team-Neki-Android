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
import com.neki.android.feature.photo_upload.impl.qrscan.component.PhotoWebView
import com.neki.android.feature.photo_upload.impl.qrscan.component.QRScannerContent

@Composable
internal fun QRScanRoute(
    viewModel: QRScanViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            QRScanSideEffect.NavigateBack -> navigateBack()
            QRScanSideEffect.NavigateToHome -> navigateToHome()
            is QRScanSideEffect.ShowToast -> {}
        }
    }
    QRScanScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun QRScanScreen(
    uiState: QRScanState = QRScanState(),
    onIntent: (QRScanIntent) -> Unit = {},
) {
    when (uiState.viewType) {
        QRScanViewType.QR_SCAN -> {
            QRScannerContent(
                modifier = Modifier.fillMaxSize(),
                isTorchEnabled = uiState.isTorchEnabled,
                onTorchClick = { onIntent(QRScanIntent.ToggleTorch) },
                onCloseClick = { onIntent(QRScanIntent.ClickCloseQRScan) },
                onQRCodeScanned = { url -> onIntent(QRScanIntent.ScanQRCode(url)) },
            )
        }

        QRScanViewType.WEB_VIEW -> {
            uiState.scannedUrl?.let { url ->
                PhotoWebView(
                    scannedUrl = url,
                    onDetectImageUrl = { imageUrl -> onIntent(QRScanIntent.DetectImageUrl(imageUrl)) },
                )
            } ?: run { onIntent(QRScanIntent.SetViewType(viewType = QRScanViewType.QR_SCAN)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QRScanScreenPreview() {
    NekiTheme {
        QRScanScreen()
    }
}
