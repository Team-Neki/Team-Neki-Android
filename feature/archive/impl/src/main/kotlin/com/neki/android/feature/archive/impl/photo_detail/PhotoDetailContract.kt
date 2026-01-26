package com.neki.android.feature.archive.impl.photo_detail

import com.neki.android.core.model.Photo

data class PhotoDetailState(
    val isLoading: Boolean = false,
    val photo: Photo = Photo(),
    val isShowDeleteDialog: Boolean = false,
)

sealed interface PhotoDetailIntent {
    // TopBar Intent
    data object ClickBackIcon : PhotoDetailIntent

    // ActionBar Intent
    data object ClickDownloadIcon : PhotoDetailIntent
    data object ClickFavoriteIcon : PhotoDetailIntent
    data class RevertFavorite(val originalFavorite: Boolean) : PhotoDetailIntent
    data object ClickDeleteIcon : PhotoDetailIntent

    // Delete Dialog Intent
    data object DismissDeleteDialog : PhotoDetailIntent
    data object ClickDeleteDialogCancelButton : PhotoDetailIntent
    data object ClickDeleteDialogConfirmButton : PhotoDetailIntent
}

sealed interface PhotoDetailSideEffect {
    data object NavigateBack : PhotoDetailSideEffect
    data object NotifyArchiveUpdated : PhotoDetailSideEffect
    data class ShowToastMessage(val message: String) : PhotoDetailSideEffect
    data class DownloadImage(val imageUrl: String) : PhotoDetailSideEffect
}
