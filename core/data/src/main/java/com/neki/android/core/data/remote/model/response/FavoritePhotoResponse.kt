package com.neki.android.core.data.remote.model.response

import com.neki.android.core.common.util.toFormattedDate
import com.neki.android.core.model.Photo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoritePhotoResponse(
    @SerialName("hasNext") val hasNext: Boolean,
    @SerialName("items") val items: List<Item>,
) {
    @Serializable
    data class Item(
        @SerialName("contentType") val contentType: String,
        @SerialName("createdAt") val createdAt: String,
        @SerialName("favorite") val favorite: Boolean,
        @SerialName("folderId") val folderId: Long?,
        @SerialName("imageUrl") val imageUrl: String,
        @SerialName("photoId") val photoId: Long,
    ) {
        internal fun toModel() = Photo(
            id = photoId,
            imageUrl = imageUrl,
            date = createdAt.toFormattedDate(),
        )
    }

    fun toModels() = items.map { it.toModel() }
}
