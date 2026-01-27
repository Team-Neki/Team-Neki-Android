package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.MediaUploadTicket
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaUploadTicketDataResponse(
    @SerialName("method") val method: String,
    @SerialName("expiresIn") val expiresIn: String,
    @SerialName("items") val items: List<MediaUploadTicketItemResponse>,
) {
    fun toModels() = items.map { it.toModel() }

    @Serializable
    data class MediaUploadTicketItemResponse(
        @SerialName("mediaId") val mediaId: Long,
        @SerialName("uploadTicket") val uploadTicket: String,
        @SerialName("contentType") val contentType: String,
    ) {
        fun toModel() = MediaUploadTicket(
            mediaId = mediaId,
            uploadUrl = uploadTicket,
        )
    }
}
