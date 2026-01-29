package com.neki.android.feature.photo_upload.api

sealed interface QRScanResult {
    data class QRCodeScanned(val imageUrl: String) : QRScanResult
    data object OpenGallery : QRScanResult
}
