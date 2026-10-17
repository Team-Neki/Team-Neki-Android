package com.neki.android.core.data.remote.model.request

import com.neki.android.core.model.SearchFilterGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    @SerialName("keyword") val keyword: String,
    @SerialName("filterGroup") val filterGroup: SearchFilterGroupRequest,
    @SerialName("userLocation") val userLocation: SearchLocationRequest? = null,
)

@Serializable
data class SearchFilterGroupRequest(
    @SerialName("brandFilter") val brandFilter: SearchBrandFilterRequest? = null,
    @SerialName("sortFilter") val sortFilter: SearchSortFilterRequest? = null,
)

@Serializable
data class SearchBrandFilterRequest(
    @SerialName("brands") val brands: List<SearchBrandIdRequest>,
)

@Serializable
data class SearchLocationRequest(
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
)

@Serializable
data class SearchBrandIdRequest(
    @SerialName("brandId") val brandId: Long,
)

@Serializable
data class SearchSortFilterRequest(
    @SerialName("type") val type: String,
    @SerialName("order") val order: String? = null,
)

internal fun SearchFilterGroup.toRequest(): SearchFilterGroupRequest = SearchFilterGroupRequest(
    brandFilter = brandIds?.let { ids ->
        SearchBrandFilterRequest(ids.map { SearchBrandIdRequest(it) })
    },
    sortFilter = sortFilter?.let { SearchSortFilterRequest(it.type, it.order) },
)
