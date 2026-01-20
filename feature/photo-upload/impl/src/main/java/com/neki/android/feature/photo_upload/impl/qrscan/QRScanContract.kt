package com.neki.android.feature.photo_upload.impl.qrscan

data class QRScanState(
    val isLoading: Boolean = false,
    val viewType: QRScanViewType = QRScanViewType.QR_SCAN,
    val scannedUrl: String? = null,
    val detectedImageUrl: String? = null,
    val showInfoDialog: Boolean = false,
    val isTorchEnabled: Boolean = false,
)

sealed interface QRScanIntent {
    data object ToggleTorch : QRScanIntent
    data object ClickCloseQRScan : QRScanIntent
    data class ScanQRCode(val scannedUrl: String) : QRScanIntent
    data class SetViewType(val viewType: QRScanViewType) : QRScanIntent
    data class DetectImageUrl(val imageUrl: String) : QRScanIntent
}

sealed interface QRScanSideEffect {
    data object NavigateBack : QRScanSideEffect
    data class NavigateToHome(val imageUrl: String) : QRScanSideEffect
    data class ShowToast(val message: String) : QRScanSideEffect
}

enum class QRScanViewType {
    QR_SCAN,
    WEB_VIEW,
}
