package com.neki.android.feature.pose.impl.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_DEFAULT_TOP_PADDING
import com.neki.android.feature.pose.impl.main.component.PoseFilterBar
import com.neki.android.feature.pose.impl.main.component.PeopleCountBottomSheet
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.feature.pose.impl.main.component.PoseListContent
import com.neki.android.feature.pose.impl.main.component.PoseTopBar
import com.neki.android.feature.pose.impl.main.component.RandomPosePeopleCountBottomSheet
import com.neki.android.feature.pose.impl.main.component.RecommendationChip
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
internal fun PoseRoute(
    viewModel: PoseViewModel = hiltViewModel(),
    navigateToPoseDetail: (Long) -> Unit,
    navigateToRandomPose: (PeopleCount) -> Unit,
    navigateToNotification: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val posePagingItems = viewModel.posePagingData.collectAsLazyPagingItems()
    val context = LocalContext.current
    val lazyState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PoseEffect.NavigateToNotification -> navigateToNotification()
            is PoseEffect.NavigateToRandomPose -> navigateToRandomPose(sideEffect.peopleCount)
            is PoseEffect.NavigateToPoseDetail -> navigateToPoseDetail(sideEffect.poseId)
            is PoseEffect.ShowToast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            PoseEffect.ScrollToTop -> coroutineScope.launch {
                snapshotFlow { posePagingItems.loadState.refresh }
                    .dropWhile { it is LoadState.NotLoading }
                    .first { it is LoadState.NotLoading }
                lazyState.scrollToItem(0)
            }
        }
    }

    PoseScreen(
        uiState = uiState,
        posePagingItems = posePagingItems,
        lazyState = lazyState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun PoseScreen(
    uiState: PoseState = PoseState(),
    posePagingItems: LazyPagingItems<Pose>,
    lazyState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onIntent: (PoseIntent) -> Unit = {},
) {
    val isRefreshing by remember {
        derivedStateOf {
            posePagingItems.loadState.refresh is LoadState.Loading && posePagingItems.itemCount == 0
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        PoseContent(
            selectedPeopleCount = uiState.selectedPeopleCount,
            isBookmarkSelected = uiState.isShowBookmarkedPose,
            posePagingItems = posePagingItems,
            lazyState = lazyState,
            onClickAlarmIcon = { onIntent(PoseIntent.ClickAlarmIcon) },
            onClickPeopleCount = { onIntent(PoseIntent.ClickPeopleCountChip) },
            onClickBookmark = { onIntent(PoseIntent.ClickBookmarkChip) },
            onClickPoseItem = { onIntent(PoseIntent.ClickPoseItem(it)) },
            onClickBookmarkIcon = { onIntent(PoseIntent.ClickBookmarkIcon(it)) },
        )

        RecommendationChip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            onClick = { onIntent(PoseIntent.ClickRandomPoseRecommendation) },
        )
    }

    if (isRefreshing) {
        LoadingDialog()
    }

    if (uiState.isShowPeopleCountBottomSheet) {
        PeopleCountBottomSheet(
            selectedItem = uiState.selectedPeopleCount,
            onDismissRequest = { onIntent(PoseIntent.DismissPeopleCountBottomSheet) },
            onClickItem = { onIntent(PoseIntent.ClickPeopleCountSheetItem(it)) },
        )
    }

    if (uiState.isShowRandomPosePeopleCountBottomSheet) {
        RandomPosePeopleCountBottomSheet(
            selectedCount = uiState.selectedRandomPosePeopleCount,
            onDismissRequest = { onIntent(PoseIntent.DismissRandomPosePeopleCountBottomSheet) },
            onOptionSelected = { onIntent(PoseIntent.ClickRandomPosePeopleCountSheetItem(it)) },
            onClickSelectButton = { onIntent(PoseIntent.ClickRandomPoseBottomSheetSelectButton) },
        )
    }
}

@Composable
fun PoseContent(
    modifier: Modifier = Modifier,
    selectedPeopleCount: PeopleCount?,
    isBookmarkSelected: Boolean,
    posePagingItems: LazyPagingItems<Pose>,
    lazyState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onClickAlarmIcon: () -> Unit = {},
    onClickPeopleCount: () -> Unit = {},
    onClickBookmark: () -> Unit = {},
    onClickPoseItem: (Pose) -> Unit = {},
    onClickBookmarkIcon: (Pose) -> Unit = {},
) {
    val density = LocalDensity.current
    var filterBarHeightPx by remember { mutableIntStateOf(0) }
    val topPadding = with(density) { filterBarHeightPx.toDp() }
    val showFilterBar by remember {
        derivedStateOf {
            !lazyState.canScrollBackward || lazyState.lastScrolledBackward
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        PoseTopBar(
            onClickIcon = onClickAlarmIcon,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            PoseListContent(
                topPadding = topPadding + POSE_LAYOUT_DEFAULT_TOP_PADDING.dp,
                posePagingItems = posePagingItems,
                state = lazyState,
                onClickItem = onClickPoseItem,
                onClickBookmark = onClickBookmarkIcon,
            )
            PoseFilterBar(
                modifier = Modifier
                    .onSizeChanged { size ->
                        if (filterBarHeightPx != 0) return@onSizeChanged
                        else filterBarHeightPx = size.height
                    },
                peopleCount = selectedPeopleCount,
                isBookmarkSelected = isBookmarkSelected,
                visible = showFilterBar,
                onClickPeopleCount = onClickPeopleCount,
                onClickBookmark = onClickBookmark,
            )
        }
    }
}
