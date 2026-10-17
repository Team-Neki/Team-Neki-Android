package com.neki.android.core.model

import androidx.compose.runtime.Immutable

enum class SearchCompletionType(val path: String) {
    REGION("regions"),
    STATION("stations"),
    PHOTO_BOOTH("photo-booths"),
}

@Immutable
data class SearchCompletion(
    val keyword: String,
    val type: SearchCompletionType,
    val distanceMeters: Int? = null,
    val filterGroup: SearchFilterGroup = SearchFilterGroup(),
)

data class SearchBrand(
    val id: Long,
    val name: String,
    val code: String,
    val count: Int,
)

data class SearchFilterGroup(
    val brandIds: List<Long>? = null,
    val sortFilter: SearchSortCondition? = null,
)

data class SearchSortCondition(
    val type: String,
    val order: String? = null,
)
