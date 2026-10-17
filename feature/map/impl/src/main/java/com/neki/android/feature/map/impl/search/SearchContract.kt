package com.neki.android.feature.map.impl.search

import com.neki.android.core.model.SearchCompletion

internal data class SearchState(
    val query: String = "",
    val submittedKeyword: String = "",
    val showDistance: Boolean = false,
)

internal sealed interface SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent
    data object SubmitSearch : SearchIntent
    data class ClickResult(val result: SearchCompletion) : SearchIntent
    data object ClickBack : SearchIntent
}

internal sealed interface SearchEffect {
    data object HideKeyboard : SearchEffect
    data object NavigateBack : SearchEffect
    data class SelectResult(val result: SearchCompletion) : SearchEffect
}
