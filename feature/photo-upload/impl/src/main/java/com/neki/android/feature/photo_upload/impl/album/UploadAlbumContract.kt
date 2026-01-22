package com.neki.android.feature.photo_upload.impl.album

import android.net.Uri
import com.neki.android.core.model.Album
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class UploadAlbumState(
    val imageUrl: String? = null,
    val selectedUris: ImmutableList<Uri> = persistentListOf(),
    val favoriteAlbum: Album = Album(),
    val albums: ImmutableList<Album> = persistentListOf(),
    val selectedAlbumIds: PersistentList<Long> = persistentListOf(),
) {
    val count: Int
        get() = if (imageUrl == null) selectedUris.size else 1
    val uploadType: UploadType
        get() = if (imageUrl == null) UploadType.GALLERY else UploadType.QR_SCAN
}

enum class UploadType {
    QR_SCAN,
    GALLERY,
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
