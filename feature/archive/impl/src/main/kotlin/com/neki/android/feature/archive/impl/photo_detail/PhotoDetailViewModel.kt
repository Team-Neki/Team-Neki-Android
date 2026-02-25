package com.neki.android.feature.archive.impl.photo_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.api.ArchiveNavKey
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
    @Assisted private val key: ArchiveNavKey.PhotoDetail,
    private val photoRepository: PhotoRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val favoriteRequests = MutableSharedFlow<Pair<Long, Boolean>>(extraBufferCapacity = 64)
    private val committedFavorites = key.photos.associate { it.id to it.isFavorite }.toMutableMap()
    private var hasNext: Boolean = key.hasNext
    private var nextPage: Int = key.photos.size / PAGE_SIZE
    private var isLoadingMore: Boolean = false
    val store: MviIntentStore<PhotoDetailState, PhotoDetailIntent, PhotoDetailSideEffect> =
        mviIntentStore(
            initialState = PhotoDetailState(
                photos = key.photos,
                currentIndex = key.initialIndex,
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
        fun create(key: ArchiveNavKey.PhotoDetail): PhotoDetailViewModel
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
            PhotoDetailIntent.ClickLeftPhoto -> {
                if (state.currentIndex > 0) {
                    postSideEffect(PhotoDetailSideEffect.AnimateToPage(state.currentIndex - 1))
                } else {
                    postSideEffect(PhotoDetailSideEffect.ShowToastMessage("첫번째 사진이에요"))
                }
            }

            PhotoDetailIntent.ClickRightPhoto -> {
                if (state.currentIndex < state.photos.lastIndex) {
                    postSideEffect(PhotoDetailSideEffect.AnimateToPage(state.currentIndex + 1))
                } else if (!hasNext) {
                    postSideEffect(PhotoDetailSideEffect.ShowToastMessage("마지막 사진이에요"))
                } else {
                    postSideEffect(PhotoDetailSideEffect.ShowToastMessage("새로운 사진을 불러오고 있어요"))
                }
            }

            is PhotoDetailIntent.PageChanged -> {
                reduce { copy(currentIndex = intent.index) }
                preloadIfNeeded(intent.index, reduce)
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

    private fun preloadIfNeeded(
        currentIndex: Int,
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
    ) {
        val latestState = store.uiState.value
        if (currentIndex >= latestState.photos.size - PRELOAD_THRESHOLD && hasNext && !isLoadingMore) {
            loadMorePhotos(reduce)
        }
    }

    private fun loadMorePhotos(
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
    ) {
        isLoadingMore = true
        viewModelScope.launch {
            try {
                val result = if (key.isFavoriteOnly) {
                    photoRepository.getFavoritePhotosPage(nextPage, PAGE_SIZE, key.sortOrder)
                } else {
                    photoRepository.getPhotosPage(key.folderId, nextPage, PAGE_SIZE, key.sortOrder)
                }
                result
                    .onSuccess { page ->
                        reduce { copy(photos = photos + page.photos) }
                        hasNext = page.hasNext
                        nextPage++
                        page.photos.forEach { committedFavorites[it.id] = it.isFavorite }
                    }
                    .onFailure { e ->
                        Timber.e(e, "loadMorePhotos failed")
                    }
            } finally {
                isLoadingMore = false
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

    companion object {
        private const val PAGE_SIZE = 5
        private const val PRELOAD_THRESHOLD = 3
    }
}
