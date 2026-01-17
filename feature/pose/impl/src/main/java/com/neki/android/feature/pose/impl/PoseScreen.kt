package com.neki.android.feature.pose.impl

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
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.pose.impl.component.FilterBar
import com.neki.android.feature.pose.impl.component.NumberOfPeopleBottomSheet
import com.neki.android.feature.pose.impl.component.PoseListContent
import com.neki.android.feature.pose.impl.component.PoseTopBar
import com.neki.android.feature.pose.impl.component.RecommendationChip
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_DEFAULT_TOP_PADDING
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun PoseRoute(
    viewModel: PoseViewModel = hiltViewModel(),
    navigateToPoseDetail: () -> Unit,
    navigateToNotification: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PoseEffect.NavigateToNotification -> navigateToNotification()
            PoseEffect.NavigateToPoseDetail -> navigateToPoseDetail()
            is PoseEffect.ShowToast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            else -> {}
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
            selectedNumberOfPeople = uiState.selectedNumberOfPeople,
            isScrapSelected = uiState.showScrappedPose,
            poseList = if (uiState.showScrappedPose) uiState.scrappedPoseList else uiState.randomPoseList,
            onAlarmIconClick = { onIntent(PoseIntent.ClickAlarmIcon) },
            onNumberOfPeopleClick = { onIntent(PoseIntent.ClickNumberOfPeopleChip) },
            onScrapClick = { onIntent(PoseIntent.ClickScrapChip) },
            onPoseItemClick = { onIntent(PoseIntent.ClickPoseItem(it)) },
        )

        RecommendationChip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            onClick = { onIntent(PoseIntent.ClickRandomPoseRecommendation) },
        )
    }

    if (uiState.showNumberOfPeopleBottomSheet) {
        NumberOfPeopleBottomSheet(
            selectedItem = uiState.selectedNumberOfPeople,
            onDismissRequest = { onIntent(PoseIntent.DismissNumberOfPeopleBottomSheet) },
            onItemClick = { onIntent(PoseIntent.ClickNumberOfPeopleSheetItem(it)) },
        )
    }
}

@Composable
fun PoseContent(
    modifier: Modifier = Modifier,
    selectedNumberOfPeople: NumberOfPeople,
    isScrapSelected: Boolean,
    poseList: ImmutableList<Pose>,
    onAlarmIconClick: () -> Unit = {},
    onNumberOfPeopleClick: () -> Unit = {},
    onScrapClick: () -> Unit = {},
    onPoseItemClick: (Pose) -> Unit = {},
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
            onIconClick = onAlarmIconClick,
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
                onItemClick = onPoseItemClick,
            )
            FilterBar(
                modifier = Modifier
                    .onSizeChanged { size ->
                        if (filterBarHeightPx != 0) return@onSizeChanged
                        else filterBarHeightPx = size.height
                    },
                numberOfPeople = selectedNumberOfPeople,
                isScrapSelected = isScrapSelected,
                visible = showFilterBar,
                onNumberOfPeopleClick = onNumberOfPeopleClick,
                onScrapClick = onScrapClick,
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
