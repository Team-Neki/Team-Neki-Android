package com.neki.android.feature.archive.impl.photo_detail

import com.neki.android.core.model.Photo
import com.neki.android.feature.archive.api.ArchiveResult

data class PhotoDetailState(
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val currentIndex: Int = 0,
    val isShowDeleteDialog: Boolean = false,
) {
    val photo: Photo get() = photos.getOrElse(currentIndex) { Photo() }
}

sealed interface PhotoDetailIntent {
    // TopBar Intent
    data object ClickBackIcon : PhotoDetailIntent

    // Pager Intent
    data object ClickLeftPhoto : PhotoDetailIntent
    data object ClickRightPhoto : PhotoDetailIntent
    data class PageChanged(val index: Int) : PhotoDetailIntent

    // ActionBar Intent
    data object ClickDownloadIcon : PhotoDetailIntent
    data object ClickFavoriteIcon : PhotoDetailIntent
    data class FavoriteCommitted(val photoId: Long, val newFavorite: Boolean) : PhotoDetailIntent
    data class RevertFavorite(val photoId: Long, val originalFavorite: Boolean) : PhotoDetailIntent
    data object ClickDeleteIcon : PhotoDetailIntent

    // Delete Dialog Intent
    data object DismissDeleteDialog : PhotoDetailIntent
    data object ClickDeleteDialogCancelButton : PhotoDetailIntent
    data object ClickDeleteDialogConfirmButton : PhotoDetailIntent
}

sealed interface PhotoDetailSideEffect {
    data object NavigateBack : PhotoDetailSideEffect
    data class NotifyPhotoUpdated(val result: ArchiveResult) : PhotoDetailSideEffect
    data class ShowToastMessage(val message: String) : PhotoDetailSideEffect
    data class DownloadImage(val imageUrl: String) : PhotoDetailSideEffect
    data class AnimateToPage(val index: Int) : PhotoDetailSideEffect
}
