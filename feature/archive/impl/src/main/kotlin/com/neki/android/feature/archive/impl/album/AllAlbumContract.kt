package com.neki.android.feature.archive.impl.album

import com.neki.android.core.model.Album
import com.neki.android.feature.archive.impl.dummyAlbums
import com.neki.android.feature.archive.impl.dummyFavoriteAlbum
import com.neki.android.feature.archive.impl.model.SelectMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AllAlbumState(
    val favoriteAlbum: Album = dummyFavoriteAlbum,
    val albums: ImmutableList<Album> = dummyAlbums,
    val selectMode: SelectMode = SelectMode.DEFAULT,
    val selectedAlbums: ImmutableList<Album> = persistentListOf(),
    val showOptionPopup: Boolean = false,
    val selectedDeleteOption: AlbumDeleteOption = AlbumDeleteOption.DELETE_WITH_PHOTOS,
    val showAddAlbumBottomSheet: Boolean = false,
    val showDeleteAlbumBottomSheet: Boolean = false,
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
    data class ClickAlbumItem(val album: Album) : AllAlbumIntent

    // Add Album BottomSheet Intent
    data object DismissAddAlbumBottomSheet : AllAlbumIntent
    data class ClickAddAlbumButton(val albumName: String) : AllAlbumIntent

    // Delete Album BottomSheet Intent
    data object DismissDeleteAlbumBottomSheet : AllAlbumIntent
    data class SelectDeleteOption(val option: AlbumDeleteOption) : AllAlbumIntent
    data object ClickDeleteConfirmButton : AllAlbumIntent
}

sealed interface AllAlbumSideEffect {
    data object NavigateBack : AllAlbumSideEffect
    data class NavigateToFavoriteAlbum(val album: Album) : AllAlbumSideEffect
    data class NavigateToAlbumDetail(val album: Album) : AllAlbumSideEffect
    data class ShowToastMessage(val message: String) : AllAlbumSideEffect
}
