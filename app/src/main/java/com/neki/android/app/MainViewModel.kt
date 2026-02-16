package com.neki.android.app

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.domain.usecase.UploadMultiplePhotoUseCase
import com.neki.android.core.domain.usecase.UploadSinglePhotoUseCase
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val uploadSinglePhotoUseCase: UploadSinglePhotoUseCase,
    private val uploadMultiplePhotoUseCase: UploadMultiplePhotoUseCase,
) : ViewModel() {

    val store: MviIntentStore<MainState, MainIntent, MainSideEffect> =
        mviIntentStore(
            initialState = MainState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: MainIntent,
        state: MainState,
        reduce: (MainState.() -> MainState) -> Unit,
        postSideEffect: (MainSideEffect) -> Unit,
    ) {
        when (intent) {
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
            is MainIntent.QRCodeScanned -> reduce { copy(scannedImageUrl = intent.imageUrl, isShowSelectWithAlbumDialog = true) }
            MainIntent.DismissSelectWithAlbumDialog -> reduce {
                copy(
                    isShowSelectWithAlbumDialog = false,
                    selectedUris = persistentListOf(),
                    scannedImageUrl = null,
                )
            }
            MainIntent.ClickUploadWithAlbum -> {
                reduce {
                    copy(
                        isShowSelectWithAlbumDialog = false,
                        scannedImageUrl = null,
                        selectedUris = persistentListOf(),
                    )
                }
                if (state.scannedImageUrl != null) {
                    postSideEffect(MainSideEffect.NavigateToUploadAlbumWithQRScan(state.scannedImageUrl))
                } else {
                    postSideEffect(MainSideEffect.NavigateToUploadAlbumWithGallery(state.selectedUris.map { it.toString() }))
                }
            }
            MainIntent.ClickUploadWithoutAlbum -> uploadWithoutAlbum(state, reduce, postSideEffect)
        }
    }

    private fun uploadWithoutAlbum(
        state: MainState,
        reduce: (MainState.() -> MainState) -> Unit,
        postSideEffect: (MainSideEffect) -> Unit,
    ) {
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

    private fun uploadSingleImage(
        imageUrl: String,
        reduce: (MainState.() -> MainState) -> Unit,
        postSideEffect: (MainSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            uploadSinglePhotoUseCase(imageUrl = imageUrl)
                .onSuccess {
                    reduce { copy(isLoading = false, scannedImageUrl = null) }
                    postSideEffect(MainSideEffect.ShowToast("이미지를 추가했어요"))
                }
                .onFailure { error ->
                    Timber.e(error)
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

            uploadMultiplePhotoUseCase(imageUris = imageUris)
                .onSuccess {
                    reduce { copy(isLoading = false, selectedUris = persistentListOf()) }
                    postSideEffect(MainSideEffect.ShowToast("이미지를 추가했어요"))
                }
                .onFailure { error ->
                    Timber.e(error)
                    reduce { copy(isLoading = false) }
                    postSideEffect(MainSideEffect.ShowToast("이미지 업로드에 실패했어요"))
                }
        }
    }
}
