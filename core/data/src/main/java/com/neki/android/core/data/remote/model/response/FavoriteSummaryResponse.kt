package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.AlbumPreview
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteSummaryResponse(
    @SerialName("latestImageUrl") val latestImageUrl: String?,
    @SerialName("totalCount") val totalCount: Int,
) {
    fun toModel() = AlbumPreview(
        id = -1L,
        title = "즐겨찾는사진",
        thumbnailUrl = latestImageUrl,
        photoCount = totalCount,
    )
}
