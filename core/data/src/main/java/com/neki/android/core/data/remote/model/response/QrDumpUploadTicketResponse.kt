package com.neki.android.core.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QrDumpUploadTicketResponse(
    @SerialName("method") val method: String,
    @SerialName("expiresIn") val expiresIn: String,
    @SerialName("uploadTicket") val uploadTicket: String,
    @SerialName("contentType") val contentType: String,
)
