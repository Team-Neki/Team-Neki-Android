package com.neki.android.feature.archive.impl.photo_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.coroutine.di.ApplicationScope
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.api.ArchiveNavKey
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
                currentPage = key.initialIndex,
                memo = key.photos.getOrNull(key.initialIndex)?.memo.orEmpty(),
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
            PhotoDetailIntent.ClickBackIcon -> {
                postSideEffect(PhotoDetailSideEffect.NavigateBack)
            }

            PhotoDetailIntent.ClickOptionIcon -> reduce { copy(isShowOptionPopup = true) }
            PhotoDetailIntent.DismissOptionPopup -> reduce { copy(isShowOptionPopup = false) }
            PhotoDetailIntent.ClickAddToAlbumOption -> {
                reduce { copy(isShowOptionPopup = false) }
                postSideEffect(PhotoDetailSideEffect.NavigateToSelectAlbum(state.photo.id))
            }

            // Pager Intent
            PhotoDetailIntent.ClickLeftPhoto -> {
                if (state.currentPage > 0) {
                    postSideEffect(PhotoDetailSideEffect.AnimateToPage(state.currentPage - 1))
                }
            }

            PhotoDetailIntent.ClickRightPhoto -> {
                if (state.currentPage < state.photos.lastIndex) {
                    postSideEffect(PhotoDetailSideEffect.AnimateToPage(state.currentPage + 1))
                }
            }

            is PhotoDetailIntent.PageChanged -> {
                reduce {
                    val newIndex = if (photos.isEmpty()) 0 else intent.page % photos.size
                    copy(
                        currentPage = intent.page,
                        memo = photos.getOrNull(newIndex)?.memo.orEmpty(),
                    )
                }
                preloadIfNeeded(reduce)
            }

            PhotoDetailIntent.PageScrollStarted -> reduce { copy(memoMode = MemoMode.Closed) }

            // ActionBar Intent
            PhotoDetailIntent.ClickDownloadIcon -> postSideEffect(PhotoDetailSideEffect.DownloadImage(state.photo.imageUrl))
            PhotoDetailIntent.ClickFavoriteIcon -> handleFavoriteToggle(state, reduce)
            is PhotoDetailIntent.FavoriteCommitted -> {
                committedFavorites[intent.photoId] = intent.newFavorite
                postSideEffect(PhotoDetailSideEffect.NotifyPhotoUpdated)
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

            // Memo Intent
            is PhotoDetailIntent.MemoTextChanged -> reduce { copy(memo = intent.text) }
            PhotoDetailIntent.ClickMemoIcon -> reduce {
                copy(memoMode = if (memoMode == MemoMode.Closed) MemoMode.Preview else MemoMode.Closed)
            }

            PhotoDetailIntent.ClickMemoMore -> reduce {
                copy(memoMode = MemoMode.Expanded)
            }

            PhotoDetailIntent.ClickMemoText -> reduce {
                copy(memoMode = MemoMode.Editing)
            }

            PhotoDetailIntent.ClickMemoFold -> reduce {
                copy(
                    memo = photo.memo,
                    memoMode = MemoMode.Preview,
                )
            }

            PhotoDetailIntent.ClickMemoCancel -> reduce {
                copy(
                    memo = photo.memo,
                    memoMode = MemoMode.Preview,
                )
            }

            is PhotoDetailIntent.ClickMemoDone -> {
                reduce {
                    copy(
                        memo = intent.memo,
                        memoMode = MemoMode.Preview,
                    )
                }
                saveMemo(state.copy(memo = intent.memo), reduce, postSideEffect)
            }

            PhotoDetailIntent.ClickDeleteIcon -> reduce { copy(isShowDeleteDialog = true) }
            is PhotoDetailIntent.PhotoCopied -> postSideEffect(PhotoDetailSideEffect.ShowActionToast(intent.albumId, intent.albumTitle))

            // Delete Dialog Intent
            PhotoDetailIntent.DismissDeleteDialog -> reduce { copy(isShowDeleteDialog = false) }
            PhotoDetailIntent.ClickDeleteDialogCancelButton -> reduce { copy(isShowDeleteDialog = false) }
            PhotoDetailIntent.ClickDeleteDialogConfirmButton -> handleDelete(state, reduce, postSideEffect)
        }
    }

    private fun saveMemo(
        state: PhotoDetailState,
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
        postSideEffect: (PhotoDetailSideEffect) -> Unit,
    ) {
        val photoId = state.photo.id
        val newMemo = state.memo
        val oldMemo = state.photo.memo
        if (newMemo == oldMemo) return
        reduce {
            copy(
                photos = photos.map { p ->
                    if (p.id == photoId) p.copy(memo = newMemo) else p
                },
            )
        }
        viewModelScope.launch {
            photoRepository.updateMemo(photoId, newMemo)
                .onSuccess {
                    postSideEffect(PhotoDetailSideEffect.NotifyPhotoUpdated)
                }
                .onFailure { e ->
                    Timber.e(e, "updateMemo failed")
                    reduce {
                        copy(
                            memo = oldMemo,
                            photos = photos.map { p ->
                                if (p.id == photoId) p.copy(memo = oldMemo) else p
                            },
                        )
                    }
                    postSideEffect(PhotoDetailSideEffect.ShowToastMessage("메모 저장에 실패했어요"))
                }
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
                    postSideEffect(PhotoDetailSideEffect.NotifyPhotoUpdated)
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
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
    ) {
        val latestState = store.uiState.value
        if (latestState.currentIndex >= latestState.photos.size - PRELOAD_THRESHOLD && hasNext && !isLoadingMore) {
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
        private const val PRELOAD_THRESHOLD = 5
    }
}
