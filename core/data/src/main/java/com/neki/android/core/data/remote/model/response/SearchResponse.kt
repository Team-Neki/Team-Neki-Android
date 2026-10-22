package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.SearchFilterGroup
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.model.SearchBrand
import com.neki.android.core.model.SearchCompletion
import com.neki.android.core.model.SearchCompletionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class SearchCompletionResponse(
    @SerialName("items") val items: List<Item>,
    @SerialName("hasNext") val hasNext: Boolean,
    @SerialName("totalCount") val totalCount: Int,
    @SerialName("filterGroup") val filterGroup: SearchFilterGroupResponse? = null,
) {
    @Serializable
    data class Item(
        @SerialName("keyword") val keyword: String,
        @SerialName("distanceKm") val distanceKm: Double? = null,
    ) {
        fun toModel(type: SearchCompletionType, filterGroup: SearchFilterGroup): SearchCompletion = SearchCompletion(
            keyword = keyword,
            type = type,
            filterGroup = filterGroup,
            distanceMeters = distanceKm?.takeIf { it.isFinite() && it >= 0 }?.let { (it * METERS_PER_KM).roundToInt() },
        )
    }
}

private const val METERS_PER_KM = 1000

@Serializable
data class SearchPhotoBoothsResponse(@SerialName("items") val items: List<Item>) {
    @Serializable
    data class Item(
        @SerialName("id") val id: String,
        @SerialName("brandName") val brandName: String,
        @SerialName("brandCode") val brandCode: String,
        @SerialName("branchName") val branchName: String,
        @SerialName("address") val address: String,
        @SerialName("latitude") val latitude: Double,
        @SerialName("longitude") val longitude: Double,
        @SerialName("distance") val distance: Int? = null,
        @SerialName("favorite") val favorite: Boolean,
    ) {
        fun toModel(): PhotoBooth = PhotoBooth(
            id = id,
            brandName = brandName,
            branchName = branchName,
            address = address,
            latitude = latitude,
            longitude = longitude,
            distance = distance ?: 0,
            favorite = favorite,
        )
    }
}

@Serializable
data class SearchFiltersResponse(@SerialName("brandFilter") val brandFilter: List<Item>) {
    @Serializable
    data class Item(
        @SerialName("id") val id: Long,
        @SerialName("name") val name: String,
        @SerialName("code") val code: String,
        @SerialName("count") val count: Int,
    ) {
        fun toModel(): SearchBrand = SearchBrand(id, name, code, count)
    }
}
