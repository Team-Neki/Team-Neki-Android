package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaUploadTicketRequest(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("filename")
    val filename: String = "",
    @SerialName("mediaType")
    val mediaType: String,
)
