package com.neki.android.feature.photo_upload.impl.qrscan

data class QRScanState(
    val isLoading: Boolean = false,

    val isCameraPermissionGranted: Boolean = false,
    val isPermissionRationaleDialogShown: Boolean = false,
    val isOpenAppSettingDialogShown: Boolean = false,

    val viewType: QRScanViewType = QRScanViewType.QR_SCAN,
    val scannedUrl: String? = null,
    val detectedImageUrl: String? = null,
    val isDownloadNeededDialogShown: Boolean = false,
    val isUnSupportedBrandDialogShown: Boolean = false,
    val isTorchEnabled: Boolean = false,
)

sealed interface QRScanIntent {
    data object GrantCameraPermission : QRScanIntent
    data object DenyCameraPermissionOnce : QRScanIntent
    data object DenyCameraPermissionPermanent : QRScanIntent

    data object DismissPermissionRationaleDialog : QRScanIntent
    data object ClickPermissionRationaleDialogCancel : QRScanIntent
    data object ClickPermissionRationaleDialogConfirm : QRScanIntent

    data object DismissOpenAppSettingDialog : QRScanIntent
    data object ClickOpenAppSettingDialogCancel : QRScanIntent
    data object ClickOpenAppSettingDialogConfirm : QRScanIntent

    data object ToggleTorch : QRScanIntent
    data object ClickCloseQRScan : QRScanIntent
    data class ScanQRCode(val scannedUrl: String) : QRScanIntent
    data class SetViewType(val viewType: QRScanViewType) : QRScanIntent
    data class DetectImageUrl(val imageUrl: String) : QRScanIntent
    data object DismissShouldDownloadDialog : QRScanIntent
    data object ClickGoDownload : QRScanIntent
    data object DismissUnSupportedBrandDialog : QRScanIntent
    data object ClickUploadGallery : QRScanIntent
    data object ClickProposeBrand : QRScanIntent
}

sealed interface QRScanSideEffect {
    data object RequestCameraPermission : QRScanSideEffect
    data object MoveAppSettings : QRScanSideEffect
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
