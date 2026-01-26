package com.neki.android.core.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteSummaryResponse(
    @SerialName("latestImageUrl") val latestImageUrl: String?,
    @SerialName("totalCount") val totalCount: Int,
)
