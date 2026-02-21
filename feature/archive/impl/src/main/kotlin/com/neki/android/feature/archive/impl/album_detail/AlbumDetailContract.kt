package com.neki.android.feature.archive.impl.album_detail

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import com.neki.android.core.model.Photo
import com.neki.android.feature.archive.impl.model.SelectMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AlbumDetailState(
    val isLoading: Boolean = false,
    val title: String = "",
    val isFavoriteAlbum: Boolean = false,
    val selectMode: SelectMode = SelectMode.DEFAULT,
    val isShowOptionPopup: Boolean = false,
    val selectedPhotos: ImmutableList<Photo> = persistentListOf(),
    val deletedPhotoIds: Set<Long> = emptySet(),
    val isShowDeleteDialog: Boolean = false,
    val isShowDeleteBottomSheet: Boolean = false,
    val selectedDeleteOption: PhotoDeleteOption = PhotoDeleteOption.REMOVE_FROM_ALBUM,

    val renameAlbumTextState: TextFieldState = TextFieldState(),
    val isShowRenameAlbumBottomSheet: Boolean = false,
)

enum class PhotoDeleteOption(val label: String) {
    REMOVE_FROM_ALBUM("앨범에서만 제거"),
    REMOVE_FROM_ALL("모든 위치에서 사진 제거"),
    ;

    override fun toString(): String = label
}

sealed interface AlbumDetailIntent {
    data object EnterAlbumDetailScreen : AlbumDetailIntent

    // TopBar Intent
    data object ClickBackIcon : AlbumDetailIntent
    data object OnBackPressed : AlbumDetailIntent
    data object ClickOptionIcon : AlbumDetailIntent
    data object ClickSelectOption : AlbumDetailIntent
    data object ClickAddPhotoOption : AlbumDetailIntent
    data object ClickRenameAlbumOption : AlbumDetailIntent
    data object ClickCancelButton : AlbumDetailIntent
    data object DismissOptionPopup : AlbumDetailIntent

    // Photo Intent
    data class ClickPhotoItem(val photo: Photo) : AlbumDetailIntent

    // ActionBar Intent
    data object ClickDownloadIcon : AlbumDetailIntent
    data object ClickDeleteIcon : AlbumDetailIntent

    // Delete Dialog Intent (for Favorite Album)
    data object DismissDeleteDialog : AlbumDetailIntent
    data object ClickDeleteDialogCancelButton : AlbumDetailIntent
    data object ClickDeleteDialogConfirmButton : AlbumDetailIntent

    // Delete BottomSheet Intent (for Regular Album)
    data object DismissDeleteBottomSheet : AlbumDetailIntent
    data class SelectDeleteOption(val option: PhotoDeleteOption) : AlbumDetailIntent
    data object ClickDeleteBottomSheetCancelButton : AlbumDetailIntent
    data object ClickDeleteBottomSheetConfirmButton : AlbumDetailIntent

    // Gallery Intent
    data class SelectGalleryImage(val uris: List<Uri>) : AlbumDetailIntent

    // Rename BottomSheet Intent
    data object DismissRenameBottomSheet : AlbumDetailIntent
    data object ClickRenameBottomSheetCancelButton : AlbumDetailIntent
    data object ClickRenameBottomSheetConfirmButton : AlbumDetailIntent

    // Result Intent
    data class PhotoDeleted(val photoIds: List<Long>) : AlbumDetailIntent
    data class ClickFavoriteIcon(val photo: Photo) : AlbumDetailIntent
    data class FavoriteChanged(val photoId: Long, val isFavorite: Boolean) : AlbumDetailIntent
}

sealed interface AlbumDetailSideEffect {
    data object NavigateBack : AlbumDetailSideEffect
    data class NavigateToPhotoDetail(val photo: Photo) : AlbumDetailSideEffect
    data class ShowToastMessage(val message: String) : AlbumDetailSideEffect
    data class DownloadImages(val imageUrls: List<String>) : AlbumDetailSideEffect
    data object OpenGallery : AlbumDetailSideEffect
    data object RefreshPhotos : AlbumDetailSideEffect
}
