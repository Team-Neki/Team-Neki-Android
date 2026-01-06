package com.neki.android.feature.photo_upload.impl.qrscan

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
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
            QRScanIntent.ClickCloseQRScan -> postSideEffect(QRScanSideEffect.NavigateBack)
            is QRScanIntent.ScanQRCode -> reduce {
                copy(
                    scannedUrl = intent.scannedUrl,
                    viewType = QRScanViewType.WEB_VIEW,
                )
            }

            is QRScanIntent.SetViewType -> {
                reduce { copy(viewType = intent.viewType) }
                postSideEffect(QRScanSideEffect.ShowToast("QR코드를 인식하지 못했습니다."))
            }

            is QRScanIntent.DetectImageUrl -> {}
        }
    }
}
