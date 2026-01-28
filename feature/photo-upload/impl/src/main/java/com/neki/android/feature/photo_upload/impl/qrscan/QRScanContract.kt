package com.neki.android.feature.photo_upload.impl.qrscan

data class QRScanState(
    val isLoading: Boolean = false,
    val viewType: QRScanViewType = QRScanViewType.QR_SCAN,
    val scannedUrl: String? = null,
    val detectedImageUrl: String? = null,
    val isShowInfoDialog: Boolean = false,
    val isShowUnSupportedBrandDialog: Boolean = false,
    val isTorchEnabled: Boolean = false,
)

sealed interface QRScanIntent {
    data object ToggleTorch : QRScanIntent
    data object ClickCloseQRScan : QRScanIntent
    data class ScanQRCode(val scannedUrl: String) : QRScanIntent
    data class SetViewType(val viewType: QRScanViewType) : QRScanIntent
    data class DetectImageUrl(val imageUrl: String) : QRScanIntent
    data object DismissUnSupportedBrandDialog : QRScanIntent
    data object ClickUploadGallery : QRScanIntent
    data object ClickProposeBrand : QRScanIntent
}

sealed interface QRScanSideEffect {
    data object NavigateBack : QRScanSideEffect
    data class SetQRScannedResult(val imageUrl: String) : QRScanSideEffect
    data class ShowToast(val message: String) : QRScanSideEffect
    data object OpenBrandProposalUrl : QRScanSideEffect
    data object SetOpenGalleryResult : QRScanSideEffect
}

enum class QRScanViewType {
    QR_SCAN,
    WEB_VIEW,
}
