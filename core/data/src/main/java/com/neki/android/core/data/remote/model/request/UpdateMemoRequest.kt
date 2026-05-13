package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMemoRequest(
    @SerialName("memo") val memo: String,
)
