package com.neki.android.feature.photo_upload.impl.album

import android.net.Uri
import com.neki.android.core.model.Album
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class UploadAlbumState(
    val imageUrl: String? = null,
    val selectedUris: ImmutableList<Uri> = persistentListOf(Uri.EMPTY),
    val favoriteAlbum: Album = Album(),
    val albums: ImmutableList<Album> = persistentListOf(
        Album(
            title = "sdf",
            thumbnailUrl = "https://tistory1.daumcdn.net/tistory/5417065/attach/2e12bf06d8f1431cbc08dbe151bd4505",
        ),
    ),
    val selectedAlbumIds: PersistentList<Long> = persistentListOf(),
) {
    val count: Int
        get() = if (imageUrl == null) selectedUris.size else 1
}

sealed interface UploadAlbumIntent {
    data object EnterUploadAlbumScreen : UploadAlbumIntent

    // TopBar Intent
    data object ClickBackIcon : UploadAlbumIntent
    data object ClickUploadButton : UploadAlbumIntent

    // Album Intent
    data class ClickAlbumItem(val albumId: Long) : UploadAlbumIntent
}

sealed interface UploadAlbumSideEffect {
    data object NavigateBack : UploadAlbumSideEffect
    data class NavigateToAlbumDetail(val albumId: Long) : UploadAlbumSideEffect
    data class ShowToastMessage(val message: String) : UploadAlbumSideEffect
}
