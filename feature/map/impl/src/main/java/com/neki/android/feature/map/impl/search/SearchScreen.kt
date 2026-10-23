package com.neki.android.feature.map.impl.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.SearchCompletion
import com.neki.android.core.model.SearchCompletionType
import com.neki.android.core.ui.component.LoadingIndicator
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.map.impl.search.component.SearchEmptyContent
import com.neki.android.feature.map.impl.search.component.SearchField
import com.neki.android.feature.map.impl.search.component.SearchNetworkErrorContent
import com.neki.android.feature.map.impl.search.component.SearchNoResultsContent
import com.neki.android.feature.map.impl.search.component.SearchPlaceItem
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
internal fun SearchRoute(
    viewModel: SearchViewModel,
    navigateBack: () -> Unit,
    onResultSelected: (SearchCompletion) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val results = viewModel.results.collectAsLazyPagingItems()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    viewModel.store.sideEffects.collectWithLifecycle { effect ->
        focusManager.clearFocus()
        keyboard?.hide()
        when (effect) {
            SearchEffect.HideKeyboard -> Unit
            SearchEffect.NavigateBack -> navigateBack()
            is SearchEffect.SelectResult -> onResultSelected(effect.result)
        }
    }

    SearchScreen(
        uiState = uiState,
        pagingResults = results,
        focusRequester = focusRequester,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun SearchScreen(
    uiState: SearchState,
    pagingResults: LazyPagingItems<SearchCompletion>,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onIntent: (SearchIntent) -> Unit = {},
) {
    val hasSubmittedSearch = uiState.submittedKeyword.isNotBlank()
    val isSearchEnabled by remember(pagingResults, hasSubmittedSearch) {
        derivedStateOf {
            (!hasSubmittedSearch || pagingResults.loadState.refresh !is LoadState.Loading) &&
                pagingResults.loadState.append !is LoadState.Loading
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white)
            .statusBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            SearchField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                query = uiState.query,
                isSearchEnabled = isSearchEnabled,
                onQueryChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                onSubmit = { if (isSearchEnabled) onIntent(SearchIntent.SubmitSearch) },
                onBack = { onIntent(SearchIntent.ClickBack) },
                focusRequester = focusRequester,
            )
            SearchContent(
                uiState = uiState,
                pagingResults = pagingResults,
                modifier = Modifier.weight(1f),
                onResultClick = { onIntent(SearchIntent.ClickResult(it)) },
            )
        }
    }
}

@Composable
private fun SearchContent(
    uiState: SearchState,
    pagingResults: LazyPagingItems<SearchCompletion>,
    modifier: Modifier = Modifier,
    onResultClick: (SearchCompletion) -> Unit = {},
) {
    val hasSubmittedSearch = uiState.submittedKeyword.isNotBlank()
    val isRefreshing by remember(pagingResults, hasSubmittedSearch) {
        derivedStateOf { hasSubmittedSearch && pagingResults.loadState.refresh is LoadState.Loading }
    }
    val isError by remember(pagingResults, hasSubmittedSearch) {
        derivedStateOf { hasSubmittedSearch && pagingResults.loadState.refresh is LoadState.Error }
    }
    val resultCount = pagingResults.itemCount
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.submittedKeyword) {
        listState.scrollToItem(0)
    }
    if (isRefreshing) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            LoadingIndicator(size = 200.dp)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            if (isError || resultCount == 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 92.dp),
                    ) {
                        when {
                            isError -> SearchNetworkErrorContent()
                            hasSubmittedSearch -> SearchNoResultsContent()
                            else -> SearchEmptyContent()
                        }
                    }
                }
            } else {
                items(resultCount) { index ->
                    val result = pagingResults[index] ?: return@items
                    SearchPlaceItem(
                        result = result,
                        distanceMeters = result.distanceMeters?.takeIf {
                            uiState.showDistance && result.type != SearchCompletionType.REGION
                        },
                        query = uiState.submittedKeyword,
                        onClick = { onResultClick(result) },
                    )
                    if (index < resultCount - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 68.dp, end = 20.dp),
                            color = NekiTheme.colorScheme.gray50,
                        )
                    }
                }
            }
            when (pagingResults.loadState.append) {
                is LoadState.Loading -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator()
                    }
                }
                is LoadState.Error -> item {
                    Text(
                        text = "결과를 더 불러오지 못했어요.",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                else -> Unit
            }
        }
    }
}

private fun previewResults() = listOf(
    SearchCompletion(
        keyword = "서울특별시 강남구",
        type = SearchCompletionType.REGION,
    ),
    SearchCompletion(
        keyword = "강남역 2호선",
        type = SearchCompletionType.STATION,
        distanceMeters = 16000,
    ),
    SearchCompletion(
        keyword = "포토그레이 강남점",
        type = SearchCompletionType.PHOTO_BOOTH,
        distanceMeters = 32400,
    ),
)

@DevicePreview
@Composable
private fun SearchEmptyPreview() {
    NekiTheme {
        SearchPreviewContent(SearchState())
    }
}

@DevicePreview
@Composable
private fun SearchResultsPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "강남",
                submittedKeyword = "강남",
                showDistance = true,
            ),
            results = previewResults(),
        )
    }
}

@DevicePreview
@Composable
private fun SearchNoResultsPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "메롱",
                submittedKeyword = "메롱",
            ),
        )
    }
}

@DevicePreview
@Composable
private fun SearchNetworkErrorPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "메롱",
                submittedKeyword = "메롱",
            ),
            refresh = LoadState.Error(IllegalStateException("Preview search error")),
        )
    }
}

@DevicePreview
@Composable
private fun SearchLoadingPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "강남",
                submittedKeyword = "강남",
            ),
            refresh = LoadState.Loading,
        )
    }
}

@Composable
private fun SearchPreviewContent(
    state: SearchState,
    results: List<SearchCompletion> = emptyList(),
    refresh: LoadState = LoadState.NotLoading(false),
) {
    val pagingResults = remember(results, refresh) {
        MutableStateFlow(
            PagingData.from(
                results,
                sourceLoadStates = LoadStates(
                    refresh = refresh,
                    prepend = LoadState.NotLoading(true),
                    append = LoadState.NotLoading(true),
                ),
            ),
        )
    }.collectAsLazyPagingItems()
    SearchScreen(
        uiState = state,
        pagingResults = pagingResults,
        modifier = Modifier.size(width = 375.dp, height = 700.dp),
    )
}
