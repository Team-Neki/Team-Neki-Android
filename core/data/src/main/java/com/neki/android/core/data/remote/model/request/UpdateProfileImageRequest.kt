package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileImageRequest(
    @SerialName("mediaId") val mediaId: Long?,
)
