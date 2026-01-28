package com.neki.android.feature.photo_upload.impl.qrscan

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.photo_upload.impl.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class QRScanViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<QRScanState, QRScanIntent, QRScanSideEffect> =
        mviIntentStore(
            initialState = QRScanState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: QRScanIntent,
        state: QRScanState,
        reduce: (QRScanState.() -> QRScanState) -> Unit,
        postSideEffect: (QRScanSideEffect) -> Unit,
    ) {
        when (intent) {
            QRScanIntent.ToggleTorch -> reduce { copy(isTorchEnabled = !this.isTorchEnabled) }
            QRScanIntent.ClickCloseQRScan -> postSideEffect(QRScanSideEffect.NavigateBack)
            is QRScanIntent.ScanQRCode -> {
                val scannedUrl = intent.scannedUrl

                if (isSupportedBrand(scannedUrl)) {
                    if (isShouldFirstDownloadBrand(scannedUrl)) {
                        reduce {
                            copy(
                                scannedUrl = intent.scannedUrl,
                                isShowShouldDownloadDialog = true,
                            )
                        }
                    } else {
                        reduce {
                            copy(
                                scannedUrl = intent.scannedUrl,
                                viewType = QRScanViewType.WEB_VIEW,
                            )
                        }
                    }
                } else {
                    reduce { copy(isShowUnSupportedBrandDialog = true) }
                }
            }

            is QRScanIntent.SetViewType -> {
                reduce { copy(viewType = intent.viewType) }
                postSideEffect(QRScanSideEffect.ShowToast("QR코드를 인식하지 못했습니다."))
            }

            is QRScanIntent.DetectImageUrl -> postSideEffect(QRScanSideEffect.SetQRScannedResult(intent.imageUrl))

            QRScanIntent.DismissShouldDownloadDialog -> reduce { copy(isShowShouldDownloadDialog = false) }
            QRScanIntent.ClickGoDownload -> reduce { copy(viewType = QRScanViewType.WEB_VIEW) }
            QRScanIntent.DismissUnSupportedBrandDialog -> reduce { copy(isShowUnSupportedBrandDialog = false) }
            QRScanIntent.ClickUploadGallery -> postSideEffect(QRScanSideEffect.SetOpenGalleryResult)
            QRScanIntent.ClickProposeBrand -> postSideEffect(QRScanSideEffect.OpenBrandProposalUrl)
        }
    }

    private fun isSupportedBrand(url: String): Boolean {
        return url.startsWith(BuildConfig.PHOTOISM_URL) ||
            url.startsWith(BuildConfig.LIFE_FOUR_CUT_URL)
    }

    private fun isShouldFirstDownloadBrand(url: String): Boolean {
        return false
    }
}
