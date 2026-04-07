package com.neki.android.feature.archive.impl.photo_detail

import com.neki.android.core.model.Photo
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

enum class MemoMode {
    Closed,
    Preview,
    Expanded,
    Editing,
}

data class PhotoDetailState(
    val isLoading: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val currentPage: Int = 0,
    val isShowDeleteDialog: Boolean = false,
    val memo: String = "",
    val memoModes: ImmutableMap<Long, MemoMode> = persistentMapOf(),
) {
    val currentIndex get() = if (photos.isEmpty()) 0 else currentPage.coerceIn(0, photos.lastIndex)
    val photo: Photo get() = photos.getOrElse(currentIndex) { Photo() }
    val currentMemoMode: MemoMode get() = memoModes[photo.id] ?: MemoMode.Closed
    fun memoModeOf(photoId: Long): MemoMode = memoModes[photoId] ?: MemoMode.Closed
}

sealed interface PhotoDetailIntent {
    // TopBar Intent
    data object ClickBackIcon : PhotoDetailIntent

    // Pager Intent
    data object ClickLeftPhoto : PhotoDetailIntent
    data object ClickRightPhoto : PhotoDetailIntent
    data class PageChanged(val page: Int) : PhotoDetailIntent

    // ActionBar Intent
    data object ClickDownloadIcon : PhotoDetailIntent
    data object ClickFavoriteIcon : PhotoDetailIntent
    data class FavoriteCommitted(val photoId: Long, val newFavorite: Boolean) : PhotoDetailIntent
    data class RevertFavorite(val photoId: Long, val originalFavorite: Boolean) : PhotoDetailIntent
    data object ClickMemoIcon : PhotoDetailIntent
    data object ClickMemoMore : PhotoDetailIntent
    data object ClickMemoText : PhotoDetailIntent
    data object ClickMemoFold : PhotoDetailIntent
    data class MemoTextChanged(val text: String) : PhotoDetailIntent
    data object ClickMemoCancel : PhotoDetailIntent
    data class ClickMemoDone(val memo: String) : PhotoDetailIntent
    data object ClickDeleteIcon : PhotoDetailIntent

    // Delete Dialog Intent
    data object DismissDeleteDialog : PhotoDetailIntent
    data object ClickDeleteDialogCancelButton : PhotoDetailIntent
    data object ClickDeleteDialogConfirmButton : PhotoDetailIntent
}

sealed interface PhotoDetailSideEffect {
    data object NavigateBack : PhotoDetailSideEffect
    data object NotifyPhotoUpdated : PhotoDetailSideEffect
    data class ShowToastMessage(val message: String) : PhotoDetailSideEffect
    data class DownloadImage(val imageUrl: String) : PhotoDetailSideEffect
    data class AnimateToPage(val index: Int) : PhotoDetailSideEffect
}
