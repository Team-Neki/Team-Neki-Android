package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoBoothPolygonRequest(
    @SerialName("coordinates") val coordinates: List<Coordinate>,
    @SerialName("brandIds") val brandIds: List<Long>,
)

@Serializable
data class Coordinate(
    @SerialName("longitude") val longitude: Double,
    @SerialName("latitude") val latitude: Double,
)
