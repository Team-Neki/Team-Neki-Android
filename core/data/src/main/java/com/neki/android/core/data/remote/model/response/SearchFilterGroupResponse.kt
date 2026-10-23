package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.SearchFilterGroup
import com.neki.android.core.model.SearchSortCondition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchFilterGroupResponse(
    @SerialName("brandFilter") val brandFilter: BrandFilter? = null,
    @SerialName("sortFilter") val sortFilter: SortFilter? = null,
) {
    @Serializable
    data class BrandFilter(@SerialName("brands") val brands: List<BrandId>)

    @Serializable
    data class BrandId(@SerialName("brandId") val brandId: Long)

    @Serializable
    data class SortFilter(
        @SerialName("type") val type: String,
        @SerialName("order") val order: String? = null,
    )

    internal fun toModel(): SearchFilterGroup = SearchFilterGroup(
        brandIds = brandFilter?.brands?.map { it.brandId },
        sortFilter = sortFilter?.let { SearchSortCondition(it.type, it.order) },
    )
}
