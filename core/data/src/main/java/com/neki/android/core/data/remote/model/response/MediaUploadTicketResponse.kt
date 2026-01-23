package com.neki.android.core.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaUploadTicketResponse(
    @SerialName("contentType") val contentType: String,
    @SerialName("expiresIn") val expiresIn: String,
    @SerialName("mediaId") val mediaId: Long,
    @SerialName("method") val method: String,
    @SerialName("uploadUrl") val uploadUrl: String,
) {
    fun toPair(): Pair<Long, String> = mediaId to uploadUrl
}
