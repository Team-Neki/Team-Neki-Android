package com.neki.android.feature.photo_upload.impl.qrscan

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.dialog.SingleButtonAlertDialog
import com.neki.android.core.designsystem.dialog.SingleButtonWithTextButtonAlertDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.photo_upload.api.QRScanResult
import com.neki.android.feature.photo_upload.impl.BuildConfig
import com.neki.android.feature.photo_upload.impl.qrscan.component.PhotoWebViewContent
import com.neki.android.feature.photo_upload.impl.qrscan.component.QRScannerContent

@Composable
internal fun QRScanRoute(
    viewModel: QRScanViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    setQRResult: (QRScanResult) -> Unit = {},
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            QRScanSideEffect.NavigateBack -> navigateBack()
            is QRScanSideEffect.SetQRScannedResult -> {
                setQRResult(QRScanResult.QRCodeScanned(sideEffect.imageUrl))
                navigateBack()
            }
            is QRScanSideEffect.ShowToast -> nekiToast.showToast(sideEffect.message)
            QRScanSideEffect.OpenBrandProposalUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, BuildConfig.BRAND_PROPOSAL_URL.toUri())
                context.startActivity(intent)
            }

            QRScanSideEffect.SetOpenGalleryResult -> {
                setQRResult(QRScanResult.OpenGallery)
                navigateBack()
            }
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
                onClickTorch = { onIntent(QRScanIntent.ToggleTorch) },
                onClickClose = { onIntent(QRScanIntent.ClickCloseQRScan) },
                onQRCodeScanned = { url -> onIntent(QRScanIntent.ScanQRCode(url)) },
            )
        }

        QRScanViewType.WEB_VIEW -> {
            if (uiState.scannedUrl != null)
                PhotoWebViewContent(
                    scannedUrl = uiState.scannedUrl,
                    onDetectImageUrl = { imageUrl -> onIntent(QRScanIntent.DetectImageUrl(imageUrl)) },
                )
            else {
                LaunchedEffect(Unit) {
                    onIntent(QRScanIntent.SetViewType(viewType = QRScanViewType.QR_SCAN))
                }
            }
        }
    }

    if (uiState.isShowShouldDownloadDialog) {
        SingleButtonAlertDialog(
            title = "갤러리에 사진을 먼저 다운받아주세요",
            content = "해당 브랜드는 웹사이트에서 사진을 저장해야\n네키에 자동으로 저장돼요",
            buttonText = "사진 다운로드하러가기",
            onDismissRequest = { onIntent(QRScanIntent.DismissShouldDownloadDialog) },
            onClick = { onIntent(QRScanIntent.ClickGoDownload) },
        )
    }

    if (uiState.isShowUnSupportedBrandDialog) {
        SingleButtonWithTextButtonAlertDialog(
            title = "지원하지 않는 브랜드예요",
            content = "갤러리에서 사진을 추가해 바로 저장할 수 있어요\n원하는 브랜드가 있다면 제안해주세요!",
            buttonText = "갤러리에서 추가하기",
            textButtonText = "브랜드 제안하기",
            onDismissRequest = { onIntent(QRScanIntent.DismissUnSupportedBrandDialog) },
            onButtonClick = { onIntent(QRScanIntent.ClickUploadGallery) },
            onTextButtonClick = { onIntent(QRScanIntent.ClickProposeBrand) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QRScanScreenPreview() {
    NekiTheme {
        QRScanScreen()
    }
}
