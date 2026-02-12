package com.neki.android.feature.photo_upload.impl.qrscan

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.neki.android.core.common.permission.CameraPermissionManager
import com.neki.android.core.common.permission.navigateToAppSettings
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
    val activity = LocalActivity.current as Activity
    val nekiToast = remember { NekiToast(context) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        when {
            isGranted -> viewModel.store.onIntent(QRScanIntent.GrantCameraPermission)
            CameraPermissionManager.shouldShowCameraRationale(activity) -> viewModel.store.onIntent(QRScanIntent.DenyCameraPermissionOnce)
            else -> viewModel.store.onIntent(QRScanIntent.DenyCameraPermissionPermanent)
        }
    }

    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(CameraPermissionManager.CAMERA_PERMISSION)
    }

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

            QRScanSideEffect.RequestCameraPermission -> cameraPermissionLauncher.launch(CameraPermissionManager.CAMERA_PERMISSION)
            QRScanSideEffect.MoveAppSettings -> navigateToAppSettings(context)
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
                isPermissionRationaleDialogShown = uiState.isPermissionRationaleDialogShown,
                isOpenAppSettingDialogShown = uiState.isOpenAppSettingDialogShown,
                isDownloadNeededDialogShown = uiState.isDownloadNeededDialogShown,
                isUnSupportedBrandDialogShown = uiState.isUnSupportedBrandDialogShown,
                isTorchEnabled = uiState.isTorchEnabled,
                onIntent = onIntent,
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
}

@Preview(showBackground = true)
@Composable
private fun QRScanScreenPreview() {
    NekiTheme {
        QRScanScreen()
    }
}
