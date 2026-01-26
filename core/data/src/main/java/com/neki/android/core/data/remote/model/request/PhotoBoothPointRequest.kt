package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoBoothPointRequest(
    @SerialName("longitude") val longitude: Double?,
    @SerialName("latitude") val latitude: Double?,
    @SerialName("radiusInMeters") val radiusInMeters: Int,
    @SerialName("brandIds") val brandIds: List<Long>,
)
