package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.PhotoBooth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoBoothPointResponse(
    @SerialName("items") val items: List<Item>,
) {
    @Serializable
    data class Item(
        @SerialName("id") val id: Long,
        @SerialName("brandName") val brandName: String,
        @SerialName("branchName") val branchName: String,
        @SerialName("address") val address: String,
        @SerialName("longitude") val longitude: Double,
        @SerialName("latitude") val latitude: Double,
        @SerialName("distance") val distance: Int,
    ) {
        internal fun toModel(): PhotoBooth = PhotoBooth(
            id = id,
            brandName = brandName,
            branchName = branchName,
            address = address,
            longitude = longitude,
            latitude = latitude,
            distance = distance,
        )
    }

    internal fun toModels(): List<PhotoBooth> = items.map { it.toModel() }
}
