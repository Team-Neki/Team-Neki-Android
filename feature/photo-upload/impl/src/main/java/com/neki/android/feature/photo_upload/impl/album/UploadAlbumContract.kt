package com.neki.android.feature.photo_upload.impl.album

import android.net.Uri
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.model.UploadType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class UploadAlbumState(
    val isLoading: Boolean = false,
    val imageUrl: String? = null,
    val selectedUris: ImmutableList<Uri> = persistentListOf(),
    val favoriteAlbum: AlbumPreview = AlbumPreview(),
    val albums: ImmutableList<AlbumPreview> = persistentListOf(),
    val selectedAlbums: PersistentList<AlbumPreview> = persistentListOf(),
) {
    val count: Int
        get() = if (imageUrl == null) selectedUris.size else 1
    val uploadType: UploadType
        get() = if (imageUrl == null) UploadType.MULTIPLE else UploadType.SINGLE
}

sealed interface UploadAlbumIntent {
    data object EnterUploadAlbumScreen : UploadAlbumIntent

    // TopBar Intent
    data object ClickBackIcon : UploadAlbumIntent
    data object ClickUploadButton : UploadAlbumIntent

    // Album Intent
    data class ClickAlbumItem(val album: AlbumPreview) : UploadAlbumIntent
}

sealed interface UploadAlbumSideEffect {
    data object NavigateBack : UploadAlbumSideEffect
    data class NavigateToAlbumDetail(val albumId: Long, val title: String) : UploadAlbumSideEffect
    data class ShowToastMessage(val message: String) : UploadAlbumSideEffect
}
