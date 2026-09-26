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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.map.impl.component.ToMapChip
import com.neki.android.feature.map.impl.search.component.SearchEmptyContent
import com.neki.android.feature.map.impl.search.component.SearchField
import com.neki.android.feature.map.impl.search.component.SearchNetworkErrorContent
import com.neki.android.feature.map.impl.search.component.SearchNoResultsContent
import com.neki.android.feature.map.impl.search.component.SearchPlaceItem
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onResultSelected: (SearchResult) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
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
        focusRequester = focusRequester,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
internal fun SearchScreen(
    uiState: SearchState,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onIntent: (SearchIntent) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white)
            .imePadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchField(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                query = uiState.query,
                onQueryChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                onSubmit = { onIntent(SearchIntent.SubmitSearch) },
                onBack = { onIntent(SearchIntent.ClickBack) },
                focusRequester = focusRequester,
            )
            SearchContent(
                uiState = uiState,
                modifier = Modifier.weight(1f),
                onResultClick = { onIntent(SearchIntent.ClickResult(it)) },
            )
        }
        ToMapChip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            onClick = { onIntent(SearchIntent.ClickBack) },
        )
    }
}

@Composable
private fun SearchContent(
    uiState: SearchState,
    modifier: Modifier = Modifier,
    onResultClick: (SearchResult) -> Unit = {},
) {
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.submittedKeyword) {
        listState.scrollToItem(0)
    }
    if (uiState.isSearching) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = NekiTheme.colorScheme.primary400,
                strokeWidth = 2.dp,
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            if (uiState.isNetworkError || uiState.results.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 92.dp),
                    ) {
                        when {
                            uiState.isNetworkError -> SearchNetworkErrorContent()
                            uiState.submittedKeyword.isNotBlank() -> SearchNoResultsContent()
                            else -> SearchEmptyContent()
                        }
                    }
                }
            } else {
                itemsIndexed(uiState.results, key = { _, result -> result.key }) { index, result ->
                    SearchPlaceItem(
                        result = result,
                        distanceMeters = result.distanceMeters?.takeIf {
                            uiState.showDistance && result.type != SearchResultType.REGION && it >= 0
                        },
                        query = uiState.submittedKeyword,
                        onClick = { onResultClick(result) },
                    )
                    if (index < uiState.results.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 68.dp, end = 20.dp),
                            color = NekiTheme.colorScheme.gray50,
                        )
                    }
                }
            }
        }
    }
}

private fun previewResults() = persistentListOf(
    SearchResult(
        keyword = "서울특별시 강남구",
        type = SearchResultType.REGION,
    ),
    SearchResult(
        keyword = "강남역 2호선",
        type = SearchResultType.STATION,
        distanceMeters = 16000,
    ),
    SearchResult(
        keyword = "포토그레이 강남점",
        type = SearchResultType.PHOTO_BOOTH,
        distanceMeters = 32400,
    ),
)

@ComponentPreview
@Composable
private fun SearchEmptyPreview() {
    NekiTheme {
        SearchPreviewContent(SearchState())
    }
}

@ComponentPreview
@Composable
private fun SearchResultsPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "강남",
                submittedKeyword = "강남",
                results = previewResults(),
                showDistance = true,
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchRegionPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "강남구",
                submittedKeyword = "강남구",
                results = persistentListOf(previewResults().first()),
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchPhotoBoothPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "강남역 포토이즘",
                submittedKeyword = "강남역 포토이즘",
                showDistance = true,
                results = persistentListOf(
                    SearchResult(
                        keyword = "포토이즘 강남역점",
                        type = SearchResultType.PHOTO_BOOTH,
                        distanceMeters = 32400,
                    ),
                    SearchResult(
                        keyword = "포토이즘 강남2호점",
                        type = SearchResultType.PHOTO_BOOTH,
                        distanceMeters = 32400,
                    ),
                    SearchResult(
                        keyword = "포토이즘 강남구청역점",
                        type = SearchResultType.PHOTO_BOOTH,
                        distanceMeters = 32400,
                    ),
                ),
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchNoPermissionPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "강남",
                submittedKeyword = "강남",
                results = previewResults(),
            ),
        )
    }
}

@ComponentPreview
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

@ComponentPreview
@Composable
private fun SearchNetworkErrorPreview() {
    NekiTheme {
        SearchPreviewContent(
            SearchState(
                query = "메롱",
                submittedKeyword = "메롱",
                isNetworkError = true,
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchLoadingPreview() {
    NekiTheme {
        SearchPreviewContent(SearchState(isSearching = true))
    }
}

@Composable
private fun SearchPreviewContent(state: SearchState) {
    SearchScreen(
        uiState = state,
        modifier = Modifier.size(width = 375.dp, height = 700.dp),
    )
}
