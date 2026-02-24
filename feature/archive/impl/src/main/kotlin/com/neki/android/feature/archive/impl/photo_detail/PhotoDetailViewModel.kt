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
    @Assisted private val photos: List<Photo>,
    @Assisted private val initialIndex: Int,
    private val photoRepository: PhotoRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val favoriteRequests = MutableSharedFlow<Pair<Long, Boolean>>(extraBufferCapacity = 64)
    private val committedFavorites = photos.associate { it.id to it.isFavorite }.toMutableMap()
    val store: MviIntentStore<PhotoDetailState, PhotoDetailIntent, PhotoDetailSideEffect> =
        mviIntentStore(
            initialState = PhotoDetailState(
                photos = photos,
                currentIndex = initialIndex,
            ),
            onIntent = ::onIntent,
        )

    init {
        viewModelScope.launch {
            favoriteRequests
                .debounce(500)
                .collect { (photoId, newFavorite) ->
                    val committedFavorite = committedFavorites[photoId] ?: return@collect
                    if (committedFavorite != newFavorite) {
                        photoRepository.updateFavorite(photoId, newFavorite)
                            .onSuccess {
                                Timber.d("updateFavorite success")
                                store.onIntent(PhotoDetailIntent.FavoriteCommitted(photoId, newFavorite))
                            }
                            .onFailure { e ->
                                Timber.e(e, "updateFavorite failed")
                                store.onIntent(PhotoDetailIntent.RevertFavorite(photoId, committedFavorite))
                            }
                    }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(photos: List<Photo>, initialIndex: Int): PhotoDetailViewModel
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

            // Pager Intent
            is PhotoDetailIntent.PageChanged -> {
                val clampedIndex = intent.index.coerceIn(0, state.photos.lastIndex)
                if (clampedIndex != state.currentIndex) {
                    reduce { copy(currentIndex = clampedIndex) }
                    postSideEffect(PhotoDetailSideEffect.ScrollToPage(clampedIndex))
                }
            }

            // ActionBar Intent
            PhotoDetailIntent.ClickDownloadIcon -> postSideEffect(PhotoDetailSideEffect.DownloadImage(state.photo.imageUrl))
            PhotoDetailIntent.ClickFavoriteIcon -> handleFavoriteToggle(state, reduce)
            is PhotoDetailIntent.FavoriteCommitted -> {
                committedFavorites[intent.photoId] = intent.newFavorite
                postSideEffect(PhotoDetailSideEffect.NotifyPhotoUpdated(ArchiveResult.FavoriteChanged(intent.photoId, intent.newFavorite)))
            }

            is PhotoDetailIntent.RevertFavorite -> {
                reduce {
                    copy(
                        photos = photos.map { p ->
                            if (p.id == intent.photoId) p.copy(isFavorite = intent.originalFavorite) else p
                        },
                    )
                }
            }

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
        val currentPhoto = state.photo
        val newFavoriteStatus = !currentPhoto.isFavorite
        viewModelScope.launch { favoriteRequests.emit(currentPhoto.id to newFavoriteStatus) }
        reduce {
            copy(
                photos = photos.map { p ->
                    if (p.id == currentPhoto.id) p.copy(isFavorite = newFavoriteStatus) else p
                },
            )
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
                    postSideEffect(PhotoDetailSideEffect.NotifyPhotoUpdated(ArchiveResult.PhotoDeleted(state.photo.id)))
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

        val state = store.uiState.value
        val currentPhoto = state.photo
        val committedFavorite = committedFavorites[currentPhoto.id] ?: return
        if (currentPhoto.isFavorite != committedFavorite) {
            applicationScope.launch {
                photoRepository.updateFavorite(currentPhoto.id, currentPhoto.isFavorite)
            }
        }
    }
}
