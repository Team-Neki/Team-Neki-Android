package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.BrandInfo
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
        internal fun toModel(): BrandInfo = BrandInfo(
            id = id,
            brandName = brandName,
            branchName = branchName,
            address = address,
            longitude = longitude,
            latitude = latitude,
            distance = distance,
        )
    }

    internal fun toModels(): List<BrandInfo> = items.map { it.toModel() }
}
