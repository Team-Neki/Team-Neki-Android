package com.neki.android.feature.photo_upload.impl.qrscan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.dataapi.repository.DiscordWebhookRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.photo_upload.impl.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class QRScanViewModel @Inject constructor(
    private val discordWebhookRepository: DiscordWebhookRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private var webViewEnteredUrl: String? = null
    private var imageDetected: Boolean = false
    private var isDownloadRequiredBrand: Boolean = false
    private val loggedUnsupportedUrls = mutableSetOf<String>()

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
            QRScanIntent.RequestCameraPermission -> postSideEffect(QRScanSideEffect.RequestCameraPermission)
            QRScanIntent.GrantCameraPermission -> reduce { copy(isCameraPermissionGranted = true) }
            QRScanIntent.DenyCameraPermissionOnce -> reduce {
                copy(
                    isPermissionRationaleDialogShown = true,
                    isCameraPermissionGranted = false,
                )
            }

            QRScanIntent.DenyCameraPermissionPermanent -> reduce {
                copy(
                    isOpenAppSettingDialogShown = true,
                    isCameraPermissionGranted = false,
                )
            }

            QRScanIntent.DismissPermissionRationaleDialog -> reduce { copy(isPermissionRationaleDialogShown = false) }
            QRScanIntent.ClickPermissionRationaleDialogCancel -> {
                reduce { copy(isPermissionRationaleDialogShown = false) }
                postSideEffect(QRScanSideEffect.NavigateBack)
            }

            QRScanIntent.ClickPermissionRationaleDialogConfirm -> {
                reduce { copy(isPermissionRationaleDialogShown = false) }
                postSideEffect(QRScanSideEffect.RequestCameraPermission)
            }

            QRScanIntent.DismissOpenAppSettingDialog -> reduce { copy(isOpenAppSettingDialogShown = false) }
            QRScanIntent.ClickOpenAppSettingDialogCancel -> {
                reduce { copy(isOpenAppSettingDialogShown = false) }
                postSideEffect(QRScanSideEffect.NavigateBack)
            }

            QRScanIntent.ClickOpenAppSettingDialogConfirm -> {
                reduce { copy(isOpenAppSettingDialogShown = false) }
                postSideEffect(QRScanSideEffect.MoveAppSettings)
            }

            QRScanIntent.ToggleTorch -> reduce { copy(isTorchEnabled = !this.isTorchEnabled) }
            QRScanIntent.ClickCloseQRScan -> postSideEffect(QRScanSideEffect.NavigateBack)
            is QRScanIntent.ScanQRCode -> {
                val scannedUrl = intent.scannedUrl

                if (isSupportedBrand(scannedUrl)) {
                    if (isShouldFirstDownloadBrand(scannedUrl)) {
                        isDownloadRequiredBrand = true
                        reduce {
                            copy(
                                scannedUrl = intent.scannedUrl,
                                isDownloadNeededDialogShown = true,
                            )
                        }
                    } else {
                        webViewEnteredUrl = scannedUrl
                        reduce {
                            copy(
                                scannedUrl = intent.scannedUrl,
                                viewType = QRScanViewType.WEB_VIEW,
                            )
                        }
                    }
                } else {
                    if (loggedUnsupportedUrls.add(scannedUrl)) {
                        viewModelScope.launch {
                            discordWebhookRepository.logUnsupportedBrandQR(scannedUrl)
                        }
                    }
                    reduce { copy(isUnSupportedBrandDialogShown = true) }
                }
            }

            is QRScanIntent.SetViewType -> {
                reduce { copy(viewType = intent.viewType) }
                postSideEffect(QRScanSideEffect.ShowToast("QR코드를 인식하지 못했습니다."))
            }

            is QRScanIntent.DetectImageUrl -> {
                imageDetected = true
                postSideEffect(QRScanSideEffect.SetQRScannedResult(intent.imageUrl))
            }

            QRScanIntent.DismissShouldDownloadDialog -> reduce { copy(isDownloadNeededDialogShown = false) }
            QRScanIntent.ClickGoDownload -> reduce {
                copy(
                    viewType = QRScanViewType.WEB_VIEW,
                    isDownloadNeededDialogShown = false,
                )
            }

            QRScanIntent.DismissUnSupportedBrandDialog -> reduce { copy(isUnSupportedBrandDialogShown = false) }
            QRScanIntent.ClickUploadGallery -> postSideEffect(QRScanSideEffect.SetOpenGalleryResult)
            QRScanIntent.ClickProposeBrand -> postSideEffect(QRScanSideEffect.OpenBrandProposalUrl)
        }
    }

    override fun onCleared() {
        super.onCleared()
        val url = webViewEnteredUrl
        if (url != null && !imageDetected && !isDownloadRequiredBrand) {
            applicationScope.launch {
                discordWebhookRepository.logWebViewExitWithoutImage(url)
            }
        }
    }

    private fun isSupportedBrand(url: String): Boolean {
        return url.contains(BuildConfig.PHOTOISM_URL) ||
            url.contains(BuildConfig.LIFE_FOUR_CUT_URL) ||
            url.contains(BuildConfig.PHOTO_SIGNATURE_URL) ||
            url.contains(BuildConfig.HARU_FILM_URL) ||
            url.contains(BuildConfig.PHOTO_GRAY_URL) ||
            url.contains(BuildConfig.MONO_MANSION_URL)
    }

    private fun isShouldFirstDownloadBrand(url: String): Boolean {
        return url.contains(BuildConfig.MONO_MANSION_URL) ||
            url.contains(BuildConfig.PHOTO_GRAY_URL) ||
            url.contains(BuildConfig.PHOTO_SIGNATURE_URL)
    }
}
