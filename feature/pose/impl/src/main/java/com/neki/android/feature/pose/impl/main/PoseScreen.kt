package com.neki.android.feature.pose.impl.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.PoseEffect
import com.neki.android.core.model.PoseIntent
import com.neki.android.core.model.PoseState
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_DEFAULT_TOP_PADDING
import com.neki.android.feature.pose.impl.main.component.FilterBar
import com.neki.android.feature.pose.impl.main.component.PeopleCountBottomSheet
import com.neki.android.feature.pose.impl.main.component.PoseListContent
import com.neki.android.feature.pose.impl.main.component.PoseTopBar
import com.neki.android.feature.pose.impl.main.component.RandomPosePeopleCountBottomSheet
import com.neki.android.feature.pose.impl.main.component.RecommendationChip
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun PoseRoute(
    viewModel: PoseViewModel = hiltViewModel(),
    navigateToPoseDetail: (Pose) -> Unit,
    navigateToRandomPose: (PeopleCount) -> Unit,
    navigateToNotification: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PoseEffect.NavigateToNotification -> navigateToNotification()
            is PoseEffect.NavigateToRandomPose -> navigateToRandomPose(sideEffect.peopleCount)
            is PoseEffect.NavigateToPoseDetail -> navigateToPoseDetail(sideEffect.pose)
            is PoseEffect.ShowToast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    PoseScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun PoseScreen(
    uiState: PoseState = PoseState(),
    onIntent: (PoseIntent) -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        PoseContent(
            selectedPeopleCount = uiState.selectedPeopleCount,
            isScrapSelected = uiState.isShowScrappedPose,
            poseList = if (uiState.isShowScrappedPose) uiState.scrappedPoseList else uiState.randomPoseList,
            onClickAlarmIcon = { onIntent(PoseIntent.ClickAlarmIcon) },
            onClickPeopleCount = { onIntent(PoseIntent.ClickPeopleCountChip) },
            onClickScrap = { onIntent(PoseIntent.ClickScrapChip) },
            onClickPoseItem = { onIntent(PoseIntent.ClickPoseItem(it)) },
        )

        RecommendationChip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            onClick = { onIntent(PoseIntent.ClickRandomPoseRecommendation) },
        )
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
    isScrapSelected: Boolean,
    poseList: ImmutableList<Pose>,
    onClickAlarmIcon: () -> Unit = {},
    onClickPeopleCount: () -> Unit = {},
    onClickScrap: () -> Unit = {},
    onClickPoseItem: (Pose) -> Unit = {},
) {
    val lazyState = rememberLazyStaggeredGridState()
    val density = LocalDensity.current
    var filterBarHeightPx by remember { mutableIntStateOf(0) }
    val topPadding = with(density) { filterBarHeightPx.toDp() }
    val showFilterBar by remember {
        derivedStateOf {
            !lazyState.canScrollBackward ||
                lazyState.lastScrolledBackward
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
                poseList = poseList,
                state = lazyState,
                onClickItem = onClickPoseItem,
            )
            FilterBar(
                modifier = Modifier
                    .onSizeChanged { size ->
                        if (filterBarHeightPx != 0) return@onSizeChanged
                        else filterBarHeightPx = size.height
                    },
                peopleCount = selectedPeopleCount,
                isScrapSelected = isScrapSelected,
                visible = showFilterBar,
                onClickPeopleCount = onClickPeopleCount,
                onClickScrap = onClickScrap,
            )
        }
    }
}

@DevicePreview
@Composable
private fun PoseScreenPreview() {
    NekiTheme {
        PoseScreen()
    }
}
