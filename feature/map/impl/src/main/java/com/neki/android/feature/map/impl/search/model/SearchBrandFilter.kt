package com.neki.android.feature.map.impl.search.model

import androidx.compose.runtime.Immutable
import com.neki.android.core.model.Brand

@Immutable
internal data class SearchBrandFilter(
    val brand: Brand,
    val count: Int,
)
