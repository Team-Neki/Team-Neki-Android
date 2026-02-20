package com.neki.android.feature.archive.impl.photo_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.api.ArchiveResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(FlowPreview::class)
@HiltViewModel(assistedFactory = PhotoDetailViewModel.Factory::class)
class PhotoDetailViewModel @AssistedInject constructor(
    @Assisted private val photo: Photo,
    private val photoRepository: PhotoRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val favoriteRequests = MutableSharedFlow<Boolean>(extraBufferCapacity = 64)
    val store: MviIntentStore<PhotoDetailState, PhotoDetailIntent, PhotoDetailSideEffect> =
        mviIntentStore(
            initialState = PhotoDetailState(photo = photo, committedFavorite = photo.isFavorite),
            onIntent = ::onIntent,
        )

    init {
        viewModelScope.launch {
            favoriteRequests
                .debounce(500)
                .collect { newFavorite ->
                    val committedFavorite = store.uiState.value.committedFavorite
                    if (committedFavorite != newFavorite) {
                        photoRepository.updateFavorite(photo.id, newFavorite)
                            .onSuccess {
                                Timber.d("updateFavorite success")
                                store.onIntent(PhotoDetailIntent.FavoriteCommitted(newFavorite))
                            }
                            .onFailure { e ->
                                Timber.e(e, "updateFavorite failed")
                                store.onIntent(PhotoDetailIntent.RevertFavorite(committedFavorite))
                            }
                    }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(photo: Photo): PhotoDetailViewModel
    }

    private fun onIntent(
        intent: PhotoDetailIntent,
        state: PhotoDetailState,
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
        postSideEffect: (PhotoDetailSideEffect) -> Unit,
    ) {
        when (intent) {
            // TopBar Intent
            PhotoDetailIntent.ClickBackIcon -> postSideEffect(PhotoDetailSideEffect.NavigateBack)

            // ActionBar Intent
            PhotoDetailIntent.ClickDownloadIcon -> postSideEffect(PhotoDetailSideEffect.DownloadImage(state.photo.imageUrl))
            PhotoDetailIntent.ClickFavoriteIcon -> handleFavoriteToggle(state, reduce)
            is PhotoDetailIntent.FavoriteCommitted -> {
                reduce { copy(committedFavorite = intent.newFavorite) }
                postSideEffect(PhotoDetailSideEffect.NotifyPhotoUpdated(ArchiveResult.FavoriteChanged(photo.id, intent.newFavorite)))
            }

            is PhotoDetailIntent.RevertFavorite -> reduce { copy(photo = photo.copy(isFavorite = intent.originalFavorite)) }
            PhotoDetailIntent.ClickDeleteIcon -> reduce { copy(isShowDeleteDialog = true) }

            // Delete Dialog Intent
            PhotoDetailIntent.DismissDeleteDialog -> reduce { copy(isShowDeleteDialog = false) }
            PhotoDetailIntent.ClickDeleteDialogCancelButton -> reduce { copy(isShowDeleteDialog = false) }
            PhotoDetailIntent.ClickDeleteDialogConfirmButton -> handleDelete(state, reduce, postSideEffect)
        }
    }

    private fun handleFavoriteToggle(
        state: PhotoDetailState,
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
    ) {
        val newFavoriteStatus = !state.photo.isFavorite
        viewModelScope.launch { favoriteRequests.emit(newFavoriteStatus) }
        reduce {
            copy(photo = state.photo.copy(isFavorite = newFavoriteStatus))
        }
    }

    private fun handleDelete(
        state: PhotoDetailState,
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
        postSideEffect: (PhotoDetailSideEffect) -> Unit,
    ) {
        reduce { copy(isLoading = true, isShowDeleteDialog = false) }

        viewModelScope.launch {
            photoRepository.deletePhoto(state.photo.id)
                .onSuccess {
                    reduce { copy(isLoading = false) }
                    postSideEffect(PhotoDetailSideEffect.NotifyPhotoUpdated(ArchiveResult.PhotoDeleted(photo.id)))
                    postSideEffect(PhotoDetailSideEffect.ShowToastMessage("사진을 삭제했어요"))
                    postSideEffect(PhotoDetailSideEffect.NavigateBack)
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce { copy(isLoading = false) }
                    postSideEffect(PhotoDetailSideEffect.ShowToastMessage("사진 삭제에 실패했어요"))
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        val currentFavorite = store.uiState.value.photo.isFavorite
        val committedFavorite = store.uiState.value.committedFavorite

        if (currentFavorite != committedFavorite) {
            applicationScope.launch {
                photoRepository.updateFavorite(photo.id, currentFavorite)
            }
        }
    }
}
