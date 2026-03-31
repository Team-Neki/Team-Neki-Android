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
        @SerialName("width") val width: Int? = null,
        @SerialName("height") val height: Int? = null,
        @SerialName("createdAt") val createdAt: String,
        @SerialName("memo") val memo: String? = null, // TODO: API에 memo 필드 추가 후 nullable 제거
    ) {
        internal fun toModel() = Photo(
            id = photoId,
            imageUrl = imageUrl,
            isFavorite = favorite,
            width = width,
            height = height,
            date = createdAt.toFormattedDate(),
            contentType = ContentType.fromString(contentType),
            memo = memo.orEmpty(),
        )
    }

    fun toModels() = items.map { it.toModel() }
}
