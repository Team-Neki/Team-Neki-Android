package com.neki.android.core.data.remote.model.response

import com.neki.android.core.common.util.toFormattedDate
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.Photo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoResponse(
    @SerialName("hasNext") val hasNext: Boolean,
    @SerialName("items") val items: List<Item>,
) {
    @Serializable
    data class Item(
        @SerialName("photoId") val photoId: Long,
        @SerialName("imageUrl") val imageUrl: String,
        @SerialName("favorite") val favorite: Boolean,
        @SerialName("contentType") val contentType: String,
        @SerialName("width") val width: Int,
        @SerialName("height") val height: Int,
        @SerialName("createdAt") val createdAt: String,
    ) {
        internal fun toModel() = Photo(
            id = photoId,
            imageUrl = imageUrl,
            isFavorite = favorite,
            width = width,
            height = height,
            date = createdAt.toFormattedDate(),
            contentType = ContentType.fromString(contentType),
        )
    }

    fun toModels() = items.map { it.toModel() }
}
