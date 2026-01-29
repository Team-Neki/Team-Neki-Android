package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateScrapRequest(
    @SerialName("scrap") val scrap: Boolean,
)
