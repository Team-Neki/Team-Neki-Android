package com.neki.android.feature.photo_upload.impl

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PhotoUploadViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<PhotoUploadState, PhotoUploadIntent, PhotoUploadSideEffect> =
        mviIntentStore(
            initialState = PhotoUploadState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: PhotoUploadIntent,
        state: PhotoUploadState,
        reduce: (PhotoUploadState.() -> PhotoUploadState) -> Unit,
        postSideEffect: (PhotoUploadSideEffect) -> Unit,
    ) {
        when (intent) {
            PhotoUploadIntent.ClickCloseQRScan -> postSideEffect(PhotoUploadSideEffect.NavigateBack)
            is PhotoUploadIntent.ScanQRCode -> {
                reduce { copy(scannedUrl = intent.scannedUrl) }
                postSideEffect(PhotoUploadSideEffect.NavigateToPhotoWebView)
            }

            is PhotoUploadIntent.DetectImageUrl -> {}
        }
    }
}
