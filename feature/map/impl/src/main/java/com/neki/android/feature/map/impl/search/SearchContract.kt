package com.neki.android.feature.map.impl.search

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class SearchState(
    val query: String = "",
    val submittedKeyword: String = "",
    val results: ImmutableList<SearchResult> = persistentListOf(),
    val showDistance: Boolean = false,
    val isSearching: Boolean = false,
    val isNetworkError: Boolean = false,
)

internal sealed interface SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent
    data object SubmitSearch : SearchIntent
    data class ClickResult(val result: SearchResult) : SearchIntent
    data object ClickBack : SearchIntent
}

internal sealed interface SearchEffect {
    data object HideKeyboard : SearchEffect
    data object NavigateBack : SearchEffect
    data class SelectResult(val result: SearchResult) : SearchEffect
}

@Immutable
internal data class SearchResult(
    val keyword: String,
    val type: SearchResultType,
    val distanceMeters: Int? = null,
) {
    val key: String get() = "${type.name}:$keyword"
}

internal enum class SearchResultType {
    REGION,
    STATION,
    PHOTO_BOOTH,
}
