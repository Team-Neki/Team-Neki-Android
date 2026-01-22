package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterPhotoRequest(
    @SerialName("folderId")
    val folderId: Long?,
    @SerialName("mediaId")
    val mediaId: Long,
    @SerialName("memo")
    val memo: String = "",
)
