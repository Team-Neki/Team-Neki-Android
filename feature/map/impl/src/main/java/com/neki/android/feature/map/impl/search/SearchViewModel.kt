package com.neki.android.feature.map.impl.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.map.api.MapNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
internal class SearchViewModel @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    private val mapRepository: MapRepository,
    @Assisted private val key: MapNavKey.Search,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(key: MapNavKey.Search): SearchViewModel
    }

    private data class Request(
        val keyword: String,
        val latitude: Double?,
        val longitude: Double?,
    )
    private val request = MutableSharedFlow<Request>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val results = request.flatMapLatest { query ->
        mapRepository.getSearchCompletionsFlow(
            keyword = query.keyword,
            latitude = query.latitude,
            longitude = query.longitude,
        )
    }.cachedIn(viewModelScope)

    val store: MviIntentStore<SearchState, SearchIntent, SearchEffect> = mviIntentStore(
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
            SearchIntent.SubmitSearch -> handleSubmitSearch(state, reduce, postSideEffect)
            is SearchIntent.ClickResult -> postSideEffect(SearchEffect.SelectResult(intent.result))
            SearchIntent.ClickBack -> postSideEffect(SearchEffect.NavigateBack)
        }
    }

    private fun handleSubmitSearch(
        state: SearchState,
        reduce: (SearchState.() -> SearchState) -> Unit,
        postSideEffect: (SearchEffect) -> Unit,
    ) {
        val keyword = state.query.trim().replace(Regex("\\s+"), " ")
        if (keyword.isBlank()) return
        val hasLocation = key.latitude != null && key.longitude != null && LocationPermissionManager.isGrantedLocationPermission(context)
        postSideEffect(SearchEffect.HideKeyboard)
        reduce { copy(submittedKeyword = keyword, showDistance = hasLocation) }
        request.tryEmit(
            Request(
                keyword = keyword,
                latitude = key.latitude.takeIf { hasLocation },
                longitude = key.longitude.takeIf { hasLocation },
            ),
        )
    }
}
