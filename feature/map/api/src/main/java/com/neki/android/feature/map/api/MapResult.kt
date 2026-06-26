package com.neki.android.feature.map.api

import com.neki.android.core.model.Brand

sealed interface MapResult {
    data class BrandOrderChanged(val orderedBrands: List<Brand>) : MapResult
}
