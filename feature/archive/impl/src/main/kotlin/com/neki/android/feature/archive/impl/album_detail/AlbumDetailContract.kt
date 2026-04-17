package com.neki.android.feature.archive.impl.album_detail

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import com.neki.android.core.model.Photo
import com.neki.android.feature.archive.impl.album.AlbumDeleteOption
import com.neki.android.feature.archive.impl.model.SelectMode
import com.neki.android.feature.select_album.api.SelectAlbumAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class AlbumFilterOption(val id: Long?, val title: String, val photoCount: Int)

data class ImportPhotoState(
    val isLoading: Boolean = false,
    val selectedAlbumId: Long? = null,
    val selectedPhotoIds: ImmutableSet<Long> = persistentSetOf(),
    val isShowAlbumDropdown: Boolean = false,
    val allAlbumOptions: ImmutableList<AlbumFilterOption> = persistentListOf(),
    val currentAlbumId: Long? = null,
) {
    val selectedAlbumOption: AlbumFilterOption?
        get() = allAlbumOptions.find { it.id == selectedAlbumId } ?: allAlbumOptions.firstOrNull()
}

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

    val isShowDeleteAlbumBottomSheet: Boolean = false,
    val selectedAlbumDeleteOption: AlbumDeleteOption = AlbumDeleteOption.DELETE_WITH_PHOTOS,

    val isShowImportPhotoBottomSheet: Boolean = false,
    val importPhotoState: ImportPhotoState = ImportPhotoState(),
)

enum class PhotoDeleteOption(val label: String) {
    REMOVE_FROM_ALBUM("앨범에서만 제거"),
    REMOVE_FROM_ALL("모든 위치에서 사진 제거"),
    ;

    override fun toString(): String = label
}

sealed interface AlbumDetailIntent {
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
    data class ClickPhotoItem(val photo: Photo, val index: Int) : AlbumDetailIntent

    // ActionBar Intent
    data object ClickDownloadIcon : AlbumDetailIntent
    data object ClickCopyIcon : AlbumDetailIntent
    data object ClickMoveIcon : AlbumDetailIntent
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

    // Delete Album BottomSheet Intent
    data object ClickDeleteAlbumOption : AlbumDetailIntent
    data object DismissDeleteAlbumBottomSheet : AlbumDetailIntent
    data class SelectAlbumDeleteOption(val option: AlbumDeleteOption) : AlbumDetailIntent
    data object ClickDeleteAlbumConfirmButton : AlbumDetailIntent

    // Result Intent
    data object RefreshPhotos : AlbumDetailIntent
    data class ClickFavoriteIcon(val photo: Photo) : AlbumDetailIntent

    // ImportPhoto BottomSheet
    data object DismissImportPhotoBottomSheet : AlbumDetailIntent
    data class SelectImportAlbum(val albumId: Long?) : AlbumDetailIntent
    data object ConfirmImport : AlbumDetailIntent
    data class ToggleImportPhoto(val photoId: Long) : AlbumDetailIntent
    data object ToggleImportAlbumDropdown : AlbumDetailIntent
    data object DismissImportAlbumDropdown : AlbumDetailIntent
}

sealed interface AlbumDetailSideEffect {
    data object NavigateBack : AlbumDetailSideEffect
    data class NavigateToPhotoDetail(val photo: Photo, val index: Int) : AlbumDetailSideEffect
    data class ShowToastMessage(val message: String) : AlbumDetailSideEffect
    data class DownloadImages(val imageUrls: List<String>) : AlbumDetailSideEffect
    data object OpenGallery : AlbumDetailSideEffect
    data object RefreshPhotos : AlbumDetailSideEffect
    data object NotifyResult : AlbumDetailSideEffect
    data class NavigateToSelectAlbum(val action: SelectAlbumAction) : AlbumDetailSideEffect
    data class PhotoImported(val albumId: Long) : AlbumDetailSideEffect
}
