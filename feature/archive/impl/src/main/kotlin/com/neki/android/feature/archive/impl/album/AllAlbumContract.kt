package com.neki.android.feature.archive.impl.album

import androidx.compose.foundation.text.input.TextFieldState
import com.neki.android.core.model.AlbumPreview
import com.neki.android.feature.archive.impl.model.SelectMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AllAlbumState(
    val isLoading: Boolean = false,
    val favoriteAlbum: AlbumPreview = AlbumPreview(),
    val albums: ImmutableList<AlbumPreview> = persistentListOf(),
    val selectMode: SelectMode = SelectMode.DEFAULT,
    val selectedAlbums: ImmutableList<AlbumPreview> = persistentListOf(),
    val isShowOptionPopup: Boolean = false,
    val selectedDeleteOption: AlbumDeleteOption = AlbumDeleteOption.DELETE_WITH_PHOTOS,
    val isShowAddAlbumBottomSheet: Boolean = false,
    val albumNameTextState: TextFieldState = TextFieldState(),
    val isShowDeleteAlbumBottomSheet: Boolean = false,
)

enum class AlbumDeleteOption(val label: String) {
    DELETE_WITH_PHOTOS("사진까지 함께 삭제"),
    DELETE_ALBUM_ONLY("사진은 유지하고 앨범만 삭제"),
    ;

    override fun toString(): String = label
}

sealed interface AllAlbumIntent {

    data object EnterAllAlbumScreen : AllAlbumIntent

    // TopBar Intent
    data object ClickBackIcon : AllAlbumIntent
    data object OnBackPressed : AllAlbumIntent
    data object ClickCreateButton : AllAlbumIntent
    data object ClickOptionIcon : AllAlbumIntent
    data object DismissOptionPopup : AllAlbumIntent
    data object ClickDeleteOptionRow : AllAlbumIntent
    data object ClickDeleteButton : AllAlbumIntent
    data object ClickCancelDeleteMode : AllAlbumIntent

    // Album Intent
    data object ClickFavoriteAlbum : AllAlbumIntent
    data class ClickAlbumItem(val album: AlbumPreview) : AllAlbumIntent

    // Add Album BottomSheet Intent
    data object DismissAddAlbumBottomSheet : AllAlbumIntent
    data object ClickAddAlbumButton : AllAlbumIntent

    // Delete Album BottomSheet Intent
    data object DismissDeleteAlbumBottomSheet : AllAlbumIntent
    data class SelectDeleteOption(val option: AlbumDeleteOption) : AllAlbumIntent
    data object ClickDeleteConfirmButton : AllAlbumIntent
}

sealed interface AllAlbumSideEffect {
    data object NavigateBack : AllAlbumSideEffect
    data class NavigateToFavoriteAlbum(val albumId: Long) : AllAlbumSideEffect
    data class NavigateToAlbumDetail(val albumId: Long, val title: String) : AllAlbumSideEffect
    data class ShowToastMessage(val message: String) : AllAlbumSideEffect
}
