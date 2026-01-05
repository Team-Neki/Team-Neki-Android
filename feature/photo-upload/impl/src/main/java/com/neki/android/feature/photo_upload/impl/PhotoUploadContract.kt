package com.neki.android.feature.photo_upload.impl

import android.net.Uri

data class PhotoUploadState(
    val scannedUrl: String? = null,
    val photoImageUrl: String? = null,
    val imagUri: Uri? = null,
)

sealed interface PhotoUploadIntent {
    data object ClickCloseQRScan : PhotoUploadIntent
    data class ScanQRCode(val scannedUrl: String) : PhotoUploadIntent
    data class DetectImageUrl(val photoImageUrl: String) : PhotoUploadIntent
}

sealed interface PhotoUploadSideEffect {
    data object NavigateBack : PhotoUploadSideEffect
    data object NavigateToPhotoWebView : PhotoUploadSideEffect
}
