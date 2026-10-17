package com.neki.android.feature.map.impl.search

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.map.api.MapNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
internal class SearchViewModel @AssistedInject constructor(
    @Assisted private val key: MapNavKey.Search,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(key: MapNavKey.Search): SearchViewModel
    }

    val store: MviIntentStore<SearchState, SearchIntent, SearchEffect> =
        mviIntentStore(
            initialState = SearchState(query = key.initialQuery),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: SearchIntent,
        state: SearchState,
        reduce: (SearchState.() -> SearchState) -> Unit,
        postSideEffect: (SearchEffect) -> Unit,
    ) {
        when (intent) {
            is SearchIntent.UpdateQuery -> reduce { copy(query = intent.query) }
            SearchIntent.SubmitSearch -> handleSubmitSearch(state, postSideEffect)
            is SearchIntent.ClickResult -> postSideEffect(SearchEffect.SelectResult(intent.result))
            SearchIntent.ClickBack -> postSideEffect(SearchEffect.NavigateBack)
        }
    }

    private fun handleSubmitSearch(
        state: SearchState,
        postSideEffect: (SearchEffect) -> Unit,
    ) {
        if (state.query.isBlank() || state.isSearching) return
        postSideEffect(SearchEffect.HideKeyboard)
        // TODO: 검색 API 연결 시 요청과 성공/실패 상태를 처리한다.
        // 응답 전에는 submittedKeyword와 results를 변경하지 않는다.
    }
}
