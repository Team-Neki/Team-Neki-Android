package com.neki.android.feature.map.api

import com.neki.android.core.model.Brand
import com.neki.android.core.model.SearchCompletionType
import com.neki.android.core.model.SearchFilterGroup

sealed interface MapResult {
    data class SearchSelected(
        val keyword: String,
        val type: SearchCompletionType,
        val filterGroup: SearchFilterGroup,
    ) : MapResult
    data class BrandOrderChanged(val orderedBrands: List<Brand>) : MapResult
}
