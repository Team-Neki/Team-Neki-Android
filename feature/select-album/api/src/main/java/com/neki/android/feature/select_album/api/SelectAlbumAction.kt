package com.neki.android.feature.select_album.api

import kotlinx.serialization.Serializable

@Serializable
sealed interface SelectAlbumAction {

    @Serializable
    data class UploadFromQR(
        val imageUrl: String,
    ) : SelectAlbumAction

    @Serializable
    data class UploadFromGallery(
        val imageUriStrings: List<String>,
    ) : SelectAlbumAction

    @Serializable
    data class MovePhotos(
        val photoIds: List<Long>,
        val sourceFolderId: Long,
    ) : SelectAlbumAction

    @Serializable
    data class CopyPhotos(
        val photoIds: List<Long>,
        val withShowToast: Boolean = true,
    ) : SelectAlbumAction
}
