package com.neki.android.feature.archive.impl.photo_detail

import androidx.lifecycle.ViewModel
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = PhotoDetailViewModel.Factory::class)
class PhotoDetailViewModel @AssistedInject constructor(
    @Assisted private val photo: Photo,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(photo: Photo): PhotoDetailViewModel
    }

    val store: MviIntentStore<PhotoDetailState, PhotoDetailIntent, PhotoDetailSideEffect> =
        mviIntentStore(
            initialState = PhotoDetailState(photo = photo),
            onIntent = ::onIntent,
        )

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
            PhotoDetailIntent.ClickDownloadIcon -> handleDownload(state, postSideEffect)
            PhotoDetailIntent.ClickFavoriteIcon -> handleFavoriteToggle(state, reduce, postSideEffect)
            PhotoDetailIntent.ClickDeleteIcon -> reduce { copy(showDeleteDialog = true) }

            // Delete Dialog Intent
            PhotoDetailIntent.DismissDeleteDialog -> reduce { copy(showDeleteDialog = false) }
            PhotoDetailIntent.ClickDeleteDialogCancelButton -> reduce { copy(showDeleteDialog = false) }
            PhotoDetailIntent.ClickDeleteDialogConfirmButton -> handleDelete(reduce, postSideEffect)
        }
    }

    private fun handleDownload(
        state: PhotoDetailState,
        postSideEffect: (PhotoDetailSideEffect) -> Unit,
    ) {
        // TODO: Download photo
        postSideEffect(PhotoDetailSideEffect.ShowToastMessage("사진을 갤러리에 다운로드했어요"))
    }

    private fun handleFavoriteToggle(
        state: PhotoDetailState,
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
        postSideEffect: (PhotoDetailSideEffect) -> Unit,
    ) {
        val newFavoriteStatus = !state.photo.isFavorite
        // TODO: Update favorite status in repository
        reduce {
            copy(photo = photo.copy(isFavorite = newFavoriteStatus))
        }
    }

    private fun handleDelete(
        reduce: (PhotoDetailState.() -> PhotoDetailState) -> Unit,
        postSideEffect: (PhotoDetailSideEffect) -> Unit,
    ) {
        // TODO: Delete photo from repository
        reduce { copy(showDeleteDialog = false) }
        postSideEffect(PhotoDetailSideEffect.ShowToastMessage("사진을 삭제했어요"))
        postSideEffect(PhotoDetailSideEffect.NavigateBack)
    }
}
