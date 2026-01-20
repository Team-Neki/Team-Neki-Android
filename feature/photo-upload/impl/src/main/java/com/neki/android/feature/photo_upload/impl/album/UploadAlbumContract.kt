package com.neki.android.feature.photo_upload.impl.album

import android.net.Uri
import com.neki.android.core.model.Album
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class UploadAlbumState(
    val selectedUris: ImmutableList<Uri> = persistentListOf(),
    val favoriteAlbum: Album = Album(),
    val albums: ImmutableList<Album> = persistentListOf(),
    val selectedAlbums: ImmutableList<Album> = persistentListOf(),
)

sealed interface UploadAlbumIntent {
    data object EnterUploadAlbumScreen : UploadAlbumIntent

    // TopBar Intent
    data object ClickBackIcon : UploadAlbumIntent
    data object ClickUploadButton : UploadAlbumIntent

    // Album Intent
    data class ClickAlbumItem(val album: Album) : UploadAlbumIntent
}

sealed interface UploadAlbumSideEffect {
    data object NavigateBack : UploadAlbumSideEffect
    data class NavigateToAlbumDetail(val album: Album) : UploadAlbumSideEffect
    data class ShowToastMessage(val message: String) : UploadAlbumSideEffect
}
