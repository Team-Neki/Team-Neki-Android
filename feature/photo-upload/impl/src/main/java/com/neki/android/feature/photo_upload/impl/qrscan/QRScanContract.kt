package com.neki.android.feature.photo_upload.impl.qrscan

data class QRScanState(
    val isLoading: Boolean = false,
    val scannedUrl: String? = null,
    val detectedImageUrl: String? = null,
    val showInfoDialog: Boolean = false,
    val showWebView: Boolean = false,
)

sealed interface QRScanIntent {
    data object ClickCloseQRScan : QRScanIntent
    data class ScanQRCode(val scannedUrl: String) : QRScanIntent
    data class DetectImageUrl(val imageUrl: String) : QRScanIntent
}

sealed interface QRScanSideEffect {
    data object NavigateBack : QRScanSideEffect
    data object NavigateToHome : QRScanSideEffect
    data class ShowToast(val message: String) : QRScanSideEffect
}
