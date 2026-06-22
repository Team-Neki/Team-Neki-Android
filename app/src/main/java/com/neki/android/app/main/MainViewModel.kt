package com.neki.android.app.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.permission.NotificationPermissionManager
import com.neki.android.core.dataapi.repository.NotificationRepository
import com.neki.android.core.domain.usecase.UploadMultiplePhotoUseCase
import com.neki.android.core.domain.usecase.UploadSinglePhotoUseCase
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.select_album.api.SelectAlbumAction
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val uploadSinglePhotoUseCase: UploadSinglePhotoUseCase,
    private val uploadMultiplePhotoUseCase: UploadMultiplePhotoUseCase,
) : ViewModel() {

    val store: MviIntentStore<MainState, MainIntent, MainSideEffect> =
        mviIntentStore(
            initialState = MainState(),
            onIntent = ::onIntent,
        )

    init {
        store.onIntent(MainIntent.EnterMainScreen)
    }

    private fun onIntent(
        intent: MainIntent,
        state: MainState,
        reduce: (MainState.() -> MainState) -> Unit,
        postSideEffect: (MainSideEffect) -> Unit,
    ) {
        when (intent) {
            MainIntent.EnterMainScreen -> updateNotificationToken()
            MainIntent.ClickAddPhotoFab -> reduce { copy(isShowAddPhotoBottomSheet = true) }
            MainIntent.DismissAddPhotoBottomSheet -> reduce { copy(isShowAddPhotoBottomSheet = false) }
            MainIntent.ClickQRScan -> {
                reduce { copy(isShowAddPhotoBottomSheet = false) }
                postSideEffect(MainSideEffect.NavigateToQRScan)
            }

            MainIntent.ClickGalleryUpload -> {
                reduce { copy(isShowAddPhotoBottomSheet = false) }
                postSideEffect(MainSideEffect.OpenGallery)
            }

            is MainIntent.SelectGalleryImage -> reduce { copy(isShowSelectWithAlbumDialog = true, selectedUris = intent.uris.toImmutableList()) }
            is MainIntent.ShareImageReceived -> reduce { copy(isShowSelectWithAlbumDialog = true, selectedUris = intent.uris.toImmutableList()) }
            is MainIntent.QRCodeScanned -> reduce { copy(scannedImageUrl = intent.imageUrl, isShowSelectWithAlbumDialog = true) }
            MainIntent.DismissSelectWithAlbumDialog -> reduce {
                copy(
                    isShowSelectWithAlbumDialog = false,
                    selectedUris = persistentListOf(),
                    scannedImageUrl = null,
                )
            }

            MainIntent.ClickUploadWithAlbum -> {
                val action = if (state.scannedImageUrl != null) {
                    SelectAlbumAction.UploadFromQR(imageUrl = state.scannedImageUrl)
                } else {
                    SelectAlbumAction.UploadFromGallery(
                        imageUriStrings = state.selectedUris.map { it.toString() },
                    )
                }
                reduce {
                    copy(
                        isShowSelectWithAlbumDialog = false,
                        scannedImageUrl = null,
                        selectedUris = persistentListOf(),
                    )
                }
                postSideEffect(MainSideEffect.NavigateToSelectAlbum(action))
            }

            MainIntent.ClickUploadWithoutAlbum -> {
                reduce { copy(isShowSelectWithAlbumDialog = false) }
                if (state.scannedImageUrl != null) {
                    uploadSingleImage(
                        imageUrl = state.scannedImageUrl,
                        reduce = reduce,
                        postSideEffect = postSideEffect,
                    )
                } else {
                    uploadMultipleImages(
                        imageUris = state.selectedUris,
                        reduce = reduce,
                        postSideEffect = postSideEffect,
                    )
                }
            }
        }
    }

    private fun updateNotificationToken() {
        viewModelScope.launch {
            val token = notificationRepository.getPushToken() ?: return@launch
            val pushAgreed = NotificationPermissionManager.isGrantedNotificationPermission(context)
            notificationRepository.updateNotification(token, pushAgreed)
                .onFailure { Timber.e(it, "Failed to update notification token") }
        }
    }

    private fun uploadSingleImage(
        imageUrl: String,
        reduce: (MainState.() -> MainState) -> Unit,
        postSideEffect: (MainSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            uploadSinglePhotoUseCase(imageUrl = imageUrl, folderId = null)
                .onSuccess {
                    reduce { copy(isLoading = false, scannedImageUrl = null) }
                    postSideEffect(MainSideEffect.ShowToast("이미지를 추가했어요"))
                    postSideEffect(MainSideEffect.RefreshArchive)
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce { copy(isLoading = false) }
                    postSideEffect(MainSideEffect.ShowToast("이미지 업로드에 실패했어요"))
                }
        }
    }

    private fun uploadMultipleImages(
        imageUris: List<Uri>,
        reduce: (MainState.() -> MainState) -> Unit,
        postSideEffect: (MainSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            uploadMultiplePhotoUseCase(imageUris = imageUris, folderId = null)
                .onSuccess {
                    reduce { copy(isLoading = false, selectedUris = persistentListOf()) }
                    postSideEffect(MainSideEffect.ShowToast("이미지를 추가했어요"))
                    postSideEffect(MainSideEffect.RefreshArchive)
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce { copy(isLoading = false) }
                    postSideEffect(MainSideEffect.ShowToast("이미지 업로드에 실패했어요"))
                }
        }
    }
}
