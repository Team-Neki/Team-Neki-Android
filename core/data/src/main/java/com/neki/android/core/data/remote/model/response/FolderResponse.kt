package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.AlbumPreview
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderResponse(
    @SerialName("items") val items: List<Item>,
) {
    @Serializable
    data class Item(
        @SerialName("folderId") val folderId: Long,
        @SerialName("name") val name: String,
        @SerialName("latestImageUrl") val latestImageUrl: String?,
        @SerialName("totalCount") val totalCount: Int,
    ) {
        internal fun toModel() = AlbumPreview(
            id = folderId,
            title = name,
            thumbnailUrl = latestImageUrl,
            photoCount = totalCount,
        )
    }

    fun toModels() = items.map { it.toModel() }
}
