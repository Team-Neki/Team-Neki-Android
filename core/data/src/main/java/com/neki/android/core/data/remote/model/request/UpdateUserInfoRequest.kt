package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserInfoRequest(
    @SerialName("mediaId") val mediaId: Long?,
    @SerialName("name") val name: String,
)
