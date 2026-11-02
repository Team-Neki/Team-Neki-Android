package com.neki.android.feature.pose.impl.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_DEFAULT_TOP_PADDING
import com.neki.android.feature.pose.api.PoseNavKey
import com.neki.android.feature.pose.impl.main.component.PoseFilterBar
import com.neki.android.feature.pose.impl.main.component.PeopleCountBottomSheet
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.pose.impl.main.component.PoseListContent
import com.neki.android.feature.pose.impl.main.component.PoseTopBar
import com.neki.android.feature.pose.impl.main.component.RandomPosePeopleCountBottomSheet
import com.neki.android.feature.pose.impl.main.component.RecommendationChip
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
internal fun PoseRoute(
    viewModel: PoseViewModel = hiltViewModel(),
    navigateToPoseDetail: (PoseNavKey.PoseDetail) -> Unit,
    navigateToRandomPose: (PeopleCount) -> Unit,
    navigateToNotification: () -> Unit,
    navigateToQRScan: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val posePagingItems = viewModel.posePagingData.collectAsLazyPagingItems()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val lazyState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.logPoseView()
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PoseEffect.NavigateToNotification -> navigateToNotification()
            PoseEffect.NavigateToQRScan -> navigateToQRScan()
            is PoseEffect.NavigateToRandomPose -> navigateToRandomPose(sideEffect.peopleCount)
            is PoseEffect.NavigateToPoseDetail -> {
                navigateToPoseDetail(
                    sideEffect.poseDetail.copy(
                        poses = posePagingItems.itemSnapshotList.items,
                        hasNext = !posePagingItems.loadState.append.endOfPaginationReached,
                    ),
                )
            }
            is PoseEffect.ShowToast -> nekiToast.showToast(sideEffect.message)
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
            uiState = uiState,
            posePagingItems = posePagingItems,
            lazyState = lazyState,
            onClickQRCodeIcon = { onIntent(PoseIntent.ClickQRScanIcon) },
//            onClickAlarmIcon = { onIntent(PoseIntent.ClickAlarmIcon) },
            onClickPeopleCount = { onIntent(PoseIntent.ClickPeopleCountChip) },
            onClickBookmark = { onIntent(PoseIntent.ClickBookmarkChip) },
            onClickRelationTag = { onIntent(PoseIntent.ClickRelationTagChip(it)) },
            onClickSortDropdown = { onIntent(PoseIntent.ClickSortDropdown) },
            onDismissSortDropdown = { onIntent(PoseIntent.DismissSortDropdown) },
            onClickSortDropdownItem = { onIntent(PoseIntent.ClickSortDropdownItem(it)) },
            onClickPoseItem = { pose, index -> onIntent(PoseIntent.ClickPoseItem(pose, index)) },
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
    uiState: PoseState,
    posePagingItems: LazyPagingItems<Pose>,
    lazyState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onClickQRCodeIcon: () -> Unit = {},
    onClickPeopleCount: () -> Unit = {},
    onClickBookmark: () -> Unit = {},
    onClickRelationTag: (PoseRelationTag) -> Unit = {},
    onClickSortDropdown: () -> Unit = {},
    onDismissSortDropdown: () -> Unit = {},
    onClickSortDropdownItem: (PoseSortType) -> Unit = {},
    onClickPoseItem: (Pose, Int) -> Unit = { _, _ -> },
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
        PoseTopBar(onClickQRCodeIcon = onClickQRCodeIcon)
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
                peopleCount = uiState.selectedPeopleCount,
                relationTag = uiState.selectedRelationTag,
                isBookmarkSelected = uiState.isShowBookmarkedPose,
                sortType = uiState.selectedSortType,
                totalPoseCount = uiState.totalPoseCount,
                isShowSortDropdown = uiState.isShowSortDropdown,
                visible = showFilterBar,
                onClickPeopleCount = onClickPeopleCount,
                onClickBookmark = onClickBookmark,
                onClickRelationTag = onClickRelationTag,
                onClickSortDropdown = onClickSortDropdown,
                onDismissSortDropdown = onDismissSortDropdown,
                onClickSortDropdownItem = onClickSortDropdownItem,
            )
        }
    }
}

@DevicePreview
@Composable
private fun PoseScreenPreview() {
    val poses = (1..6).map { Pose(id = it.toLong(), width = 162, height = 242) }
    val posePagingItems = flowOf(PagingData.from(poses)).collectAsLazyPagingItems()

    NekiTheme {
        PoseScreen(
            uiState = PoseState(totalPoseCount = 98),
            posePagingItems = posePagingItems,
        )
    }
}

@DevicePreview
@Composable
private fun PoseScreenRelationTagSelectedPreview() {
    val poses = (1..6).map { Pose(id = it.toLong(), width = 162, height = 242) }
    val posePagingItems = flowOf(PagingData.from(poses)).collectAsLazyPagingItems()

    NekiTheme {
        PoseScreen(
            uiState = PoseState(
                selectedPeopleCount = PeopleCount.TWO,
                selectedRelationTag = PoseRelationTag.FRIEND,
                selectedSortType = PoseSortType.POPULAR,
                totalPoseCount = 20,
            ),
            posePagingItems = posePagingItems,
        )
    }
}
