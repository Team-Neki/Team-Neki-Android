package com.neki.android.feature.photo_upload.impl.qrscan.util

import android.graphics.RectF
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber

class QRImageAnalyzer(
    private val scanAreaRatio: () -> RectF?,
    private val onQRCodeScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val qrScannerOptions: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(qrScannerOptions)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees,
            )

            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val isRotated = rotationDegrees == 90 || rotationDegrees == 270

            val rotatedWidth = if (isRotated) imageProxy.height.toFloat() else imageProxy.width.toFloat()
            val rotatedHeight = if (isRotated) imageProxy.width.toFloat() else imageProxy.height.toFloat()

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.valueType == Barcode.TYPE_URL) {
                            val url = barcode.url?.url ?: continue
                            val boundingBox = barcode.boundingBox ?: continue

                            val centerXRatio = boundingBox.centerX() / rotatedWidth
                            val centerYRatio = boundingBox.centerY() / rotatedHeight

                            val scanArea = scanAreaRatio()
                            if (scanArea == null || isInScanArea(centerXRatio, centerYRatio, scanArea)) {
                                onQRCodeScanned(url)
                            }
                        }
                    }
                }
                .addOnFailureListener { e -> Timber.Forest.e(e, "Barcode scanning failed") }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }

    private fun isInScanArea(xRatio: Float, yRatio: Float, scanArea: RectF): Boolean {
        return xRatio >= scanArea.left &&
            xRatio <= scanArea.right &&
            yRatio >= scanArea.top &&
            yRatio <= scanArea.bottom
    }
}
