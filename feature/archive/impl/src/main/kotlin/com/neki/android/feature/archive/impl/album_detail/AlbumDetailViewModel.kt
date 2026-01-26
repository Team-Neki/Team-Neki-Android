package com.neki.android.feature.archive.impl.album_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.impl.model.SelectMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = AlbumDetailViewModel.Factory::class)
class AlbumDetailViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    @Assisted private val isFavoriteAlbum: Boolean,
    private val photoRepository: PhotoRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(id: Long, isFavoriteAlbum: Boolean): AlbumDetailViewModel
    }

    private val dummyPhotos = persistentListOf(
        Photo(id = 1001, imageUrl = "https://picsum.photos/seed/detail1/400/520", isFavorite = false, date = "2025.01.15"),
        Photo(id = 1002, imageUrl = "https://picsum.photos/seed/detail2/400/680", isFavorite = true, date = "2025.01.14"),
        Photo(id = 1003, imageUrl = "https://picsum.photos/seed/detail3/400/450", isFavorite = false, date = "2025.01.13"),
        Photo(id = 1004, imageUrl = "https://picsum.photos/seed/detail4/400/600", isFavorite = true, date = "2025.01.12"),
        Photo(id = 1005, imageUrl = "https://picsum.photos/seed/detail5/400/550", isFavorite = false, date = "2025.01.11"),
        Photo(id = 1006, imageUrl = "https://picsum.photos/seed/detail6/400/720", isFavorite = false, date = "2025.01.10"),
        Photo(id = 1007, imageUrl = "https://picsum.photos/seed/detail7/400/480", isFavorite = true, date = "2025.01.09"),
        Photo(id = 1008, imageUrl = "https://picsum.photos/seed/detail8/400/650", isFavorite = false, date = "2025.01.08"),
    )

    val store: MviIntentStore<AlbumDetailState, AlbumDetailIntent, AlbumDetailSideEffect> =
        mviIntentStore(
            initialState = AlbumDetailState(
                isFavoriteAlbum = isFavoriteAlbum,
            ),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(AlbumDetailIntent.EnterAlbumDetailScreen) },
        )

    private fun onIntent(
        intent: AlbumDetailIntent,
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        when (intent) {
            // TopBar Intent
            AlbumDetailIntent.EnterAlbumDetailScreen -> {
                if (isFavoriteAlbum) fetchFavoriteData(reduce)
                else fetchAlbumData(id, reduce)
            }

            AlbumDetailIntent.ClickBackIcon -> handleBackClick(state, reduce, postSideEffect)
            AlbumDetailIntent.OnBackPressed -> handleBackClick(state, reduce, postSideEffect)
            AlbumDetailIntent.ClickSelectButton -> reduce { copy(selectMode = SelectMode.SELECTING) }
            AlbumDetailIntent.ClickCancelButton -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedPhotos = persistentListOf(),
                )
            }

            // Photo Intent
            is AlbumDetailIntent.ClickPhotoItem -> handlePhotoClick(intent.photo, state, reduce, postSideEffect)

            // ActionBar Intent
            AlbumDetailIntent.ClickDownloadIcon -> handleDownload(state, postSideEffect)
            AlbumDetailIntent.ClickDeleteIcon -> handleDeleteIconClick(state, reduce, postSideEffect)

            // Delete Dialog Intent (for Favorite Album)
            AlbumDetailIntent.DismissDeleteDialog -> reduce { copy(isShowDeleteDialog = false) }
            AlbumDetailIntent.ClickDeleteDialogCancelButton -> reduce { copy(isShowDeleteDialog = false) }
            AlbumDetailIntent.ClickDeleteDialogConfirmButton -> handleFavoriteDelete(state, reduce, postSideEffect)

            // Delete BottomSheet Intent (for Regular Album)
            AlbumDetailIntent.DismissDeleteBottomSheet -> reduce { copy(isShowDeleteBottomSheet = false) }
            is AlbumDetailIntent.SelectDeleteOption -> reduce { copy(selectedDeleteOption = intent.option) }
            AlbumDetailIntent.ClickDeleteBottomSheetCancelButton -> reduce { copy(isShowDeleteBottomSheet = false) }
            AlbumDetailIntent.ClickDeleteBottomSheetConfirmButton -> handleAlbumDelete(state, reduce, postSideEffect)
        }
    }

    private fun fetchFavoriteData(
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
    ) {
        viewModelScope.launch {
            photoRepository.getFavoritePhotos()
                .onSuccess { data ->
                    reduce { copy(selectedPhotos = data.toImmutableList()) }
                }
                .onFailure { error ->
                    Timber.e(error)
                }
        }
    }

    private fun fetchAlbumData(
        id: Long,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
    ) {
        // TODO: Fetch album from repository
        reduce {
            copy(
                photoList = dummyPhotos,
            )
        }
    }

    private fun handleBackClick(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> postSideEffect(AlbumDetailSideEffect.NavigateBack)
            SelectMode.SELECTING -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedPhotos = persistentListOf(),
                )
            }
        }
    }

    private fun handlePhotoClick(
        photo: Photo,
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> {
                postSideEffect(AlbumDetailSideEffect.NavigateToPhotoDetail(photo))
            }

            SelectMode.SELECTING -> {
                val isSelected = state.selectedPhotos.any { it.id == photo.id }
                if (isSelected) {
                    reduce {
                        copy(selectedPhotos = selectedPhotos.filter { it.id != photo.id }.toImmutableList())
                    }
                } else {
                    reduce {
                        copy(selectedPhotos = (selectedPhotos + photo).toImmutableList())
                    }
                }
            }
        }
    }

    private fun handleDownload(
        state: AlbumDetailState,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        if (state.selectedPhotos.isEmpty()) {
            postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진을 선택해주세요."))
            return
        }
        postSideEffect(AlbumDetailSideEffect.DownloadImages(state.selectedPhotos.map { it.imageUrl }))
    }

    private fun handleDeleteIconClick(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        if (state.selectedPhotos.isEmpty()) {
            postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진을 선택해주세요."))
            return
        }

        if (state.isFavoriteAlbum) {
            reduce { copy(isShowDeleteDialog = true) }
        } else {
            reduce { copy(isShowDeleteBottomSheet = true) }
        }
    }

    private fun handleFavoriteDelete(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        // TODO: Delete photos from favorite album
        reduce {
            copy(
                photoList = photoList.filter { photo ->
                    selectedPhotos.none { it.id == photo.id }
                }.toImmutableList(),
                selectedPhotos = persistentListOf(),
                selectMode = SelectMode.DEFAULT,
                isShowDeleteDialog = false,
            )
        }
        postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진을 삭제했어요"))
    }

    private fun handleAlbumDelete(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        // TODO: Delete photos based on selectedDeleteOption
        reduce {
            copy(
                photoList = photoList.filter { photo ->
                    selectedPhotos.none { it.id == photo.id }
                }.toImmutableList(),
                selectedPhotos = persistentListOf(),
                selectMode = SelectMode.DEFAULT,
                isShowDeleteBottomSheet = false,
            )
        }

        val message = when (state.selectedDeleteOption) {
            PhotoDeleteOption.REMOVE_FROM_ALBUM -> "앨범에서 사진을 제거했어요"
            PhotoDeleteOption.REMOVE_FROM_ALL -> "사진을 삭제했어요"
        }
        postSideEffect(AlbumDetailSideEffect.ShowToastMessage(message))
    }
}
