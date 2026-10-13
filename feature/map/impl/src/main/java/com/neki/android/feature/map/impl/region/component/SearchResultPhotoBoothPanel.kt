package com.neki.android.feature.map.impl.region.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.modifier.buttonShadow
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.modifier.dropdownShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.ui.component.LoadingIndicator
import com.neki.android.feature.map.impl.DragLevel
import com.neki.android.feature.map.impl.component.CurrentLocationButton
import com.neki.android.feature.map.impl.component.DistanceInfo
import com.neki.android.feature.map.impl.component.HorizontalBrandItem
import com.neki.android.feature.map.impl.search.component.SearchNoResultsContent
import com.neki.android.feature.map.impl.util.rememberAnchoredPanelState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.drop

private val FirstPanelHeight = 116.dp
private val SecondPanelHeight = 286.dp
private val PanelButtonGap = 12.dp
private val SearchBarBottomSpace = 68.dp // 검색바 위 8 + 검색바 48 + 패널과 간격 12

@Composable
internal fun SearchResultPhotoBoothPanel(
    photoBooths: ImmutableList<PhotoBooth>,
    showDistance: Boolean,
    isCurrentLocation: Boolean,
    modifier: Modifier = Modifier,
    dragLevel: DragLevel = DragLevel.SECOND,
    isLoading: Boolean = false,
    isError: Boolean = false,
    onDragLevelChanged: (DragLevel) -> Unit = {},
    onShowList: () -> Unit = {},
    onCurrentLocationClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onBoothClick: (PhotoBooth) -> Unit = {},
    onFavoriteClick: (PhotoBooth) -> Unit = {},
) {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    val listState = rememberLazyListState()
    BoxWithConstraints(modifier.fillMaxSize().clipToBounds()) {
        // 이 영역은 이미 하단 탭바와 navigation bar를 제외한 지도 viewport다.
        val viewportHeight = with(density) { maxHeight.toPx() }
        val first = (viewportHeight - with(density) { FirstPanelHeight.toPx() }).coerceAtLeast(0f)
        val third = (statusBarHeight + with(density) { SearchBarBottomSpace.toPx() }).coerceAtMost(first)
        val second = (viewportHeight - with(density) { SecondPanelHeight.toPx() }).coerceIn(third, first)
        val anchors = DraggableAnchors {
            DragLevel.FIRST at first
            DragLevel.SECOND at second
            DragLevel.THIRD at third
        }
        val state = rememberSearchPanelState(dragLevel, anchors, onDragLevelChanged)
        val nestedScroll = remember(state, listState) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (source != NestedScrollSource.UserInput) return Offset.Zero
                    return if (available.y < 0f || !listState.canScrollBackward) {
                        Offset(0f, state.dispatchRawDelta(available.y))
                    } else {
                        Offset.Zero
                    }
                }

                override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
                    if (source == NestedScrollSource.UserInput) Offset(0f, state.dispatchRawDelta(available.y)) else Offset.Zero

                override suspend fun onPreFling(available: Velocity): Velocity {
                    val offset = state.requireOffset()
                    val canExpand = available.y < 0f && offset > state.anchors.minPosition()
                    val canCollapse = available.y > 0f && !listState.canScrollBackward && offset < state.anchors.maxPosition()
                    if (canExpand || canCollapse) {
                        state.settle(available.y)
                        return Velocity(0f, available.y)
                    }
                    return Velocity.Zero
                }

                override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                    state.settle(available.y)
                    return Velocity(0f, available.y)
                }
            }
        }
        val panelHeight = with(density) { (viewportHeight - state.requireOffset()).toDp() }
        val isCollapsed = state.requireOffset() >= first - 0.5f
        val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

        if (state.requireOffset() >= second && second > third) {
            CurrentLocationButton(
                isActiveCurrentLocation = isCurrentLocation,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = panelHeight + PanelButtonGap)
                    .semantics { contentDescription = "현재 위치로 이동" },
                onClick = onCurrentLocationClick,
            )
        }
        if (isCollapsed && dragLevel == DragLevel.FIRST) {
            ShowSearchListButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = panelHeight + PanelButtonGap),
                onClick = onShowList,
            )
        }
        SearchResultPanelContent(
            photoBooths = photoBooths,
            showDistance = showDistance,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(panelHeight)
                .dropdownShadow(shape = shape)
                .clip(shape)
                .background(NekiTheme.colorScheme.white),
            headerModifier = Modifier
                .anchoredDraggable(state, Orientation.Vertical)
                .semantics {
                    stateDescription = when (dragLevel) {
                        DragLevel.FIRST -> "검색 결과 패널 접힘"
                        DragLevel.SECOND -> "검색 결과 패널 중간 높이"
                        else -> "검색 결과 패널 펼쳐짐"
                    }
                    if (dragLevel != DragLevel.THIRD) {
                        expand {
                            onDragLevelChanged(if (dragLevel == DragLevel.FIRST) DragLevel.SECOND else DragLevel.THIRD)
                            true
                        }
                    }
                    if (dragLevel != DragLevel.FIRST) {
                        collapse {
                            onDragLevelChanged(if (dragLevel == DragLevel.THIRD) DragLevel.SECOND else DragLevel.FIRST)
                            true
                        }
                    }
                },
            bodyModifier = Modifier.anchoredDraggable(
                state = state,
                orientation = Orientation.Vertical,
                enabled = isLoading || isError || photoBooths.isEmpty(),
            ),
            listState = listState,
            listModifier = Modifier.nestedScroll(nestedScroll),
            isCollapsed = isCollapsed,
            isLoading = isLoading,
            isError = isError,
            onFilterClick = onFilterClick,
            onBoothClick = onBoothClick,
            onFavoriteClick = onFavoriteClick,
        )
    }
}

@Composable
private fun rememberSearchPanelState(
    level: DragLevel,
    anchors: DraggableAnchors<DragLevel>,
    onLevelChanged: (DragLevel) -> Unit,
): AnchoredDraggableState<DragLevel> {
    val state = rememberAnchoredPanelState(level, anchors)
    val currentLevel by rememberUpdatedState(level)
    val currentOnLevelChanged by rememberUpdatedState(onLevelChanged)
    var isProgrammaticTransition by remember { mutableStateOf(false) }
    LaunchedEffect(level, state) {
        isProgrammaticTransition = true
        try {
            state.animateTo(level)
        } finally {
            isProgrammaticTransition = false
        }
    }
    LaunchedEffect(state) {
        snapshotFlow { state.settledValue }.drop(1).collect { settled ->
            if (!isProgrammaticTransition && settled != currentLevel) currentOnLevelChanged(settled)
        }
    }
    return state
}

@Composable
private fun SearchResultPanelContent(
    photoBooths: ImmutableList<PhotoBooth>,
    showDistance: Boolean,
    modifier: Modifier = Modifier,
    headerModifier: Modifier = Modifier,
    bodyModifier: Modifier = Modifier,
    listModifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    isCollapsed: Boolean = false,
    isLoading: Boolean = false,
    isError: Boolean = false,
    onFilterClick: () -> Unit = {},
    onBoothClick: (PhotoBooth) -> Unit = {},
    onFavoriteClick: (PhotoBooth) -> Unit = {},
) {
    val brandNames = photoBooths.map { it.brandName }.distinct()
    Column(modifier = modifier.background(NekiTheme.colorScheme.white), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column(modifier = headerModifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BottomSheetDragHandle(color = NekiTheme.colorScheme.gray100)
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = buildAnnotatedString {
                    withStyle(NekiTheme.typography.body14SemiBold.toSpanStyle().copy(color = NekiTheme.colorScheme.gray600)) {
                        append(photoBooths.size.toString())
                    }
                    append("곳의 포토부스를 찾았어요.")
                },
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.gray400,
            )
            BrandFilterChip(
                brandName = brandNames.firstOrNull(),
                extraCount = (brandNames.size - 1).coerceAtLeast(0),
                enabled = !isLoading && !isError,
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = onFilterClick,
            )
        }
        // 접힘 도중에도 스크롤/드래그 노드를 유지해 정착 처리를 끝낸다.
        Box(
            bodyModifier
                .fillMaxWidth()
                .weight(1f)
                .clipToBounds()
                .graphicsLayer { alpha = if (isCollapsed) 0f else 1f }
                .then(if (isCollapsed) Modifier.clearAndSetSemantics {} else Modifier),
        ) {
            when {
                isLoading -> LoadingIndicator(Modifier.align(Alignment.Center), size = 200.dp)
                isError -> Text(
                    text = "검색 결과를 불러오지 못했어요.",
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 20.dp),
                    style = NekiTheme.typography.body14Medium,
                    color = NekiTheme.colorScheme.gray500,
                    textAlign = TextAlign.Center,
                )
                photoBooths.isEmpty() -> BoxWithConstraints(Modifier.fillMaxSize()) {
                    // 3단계는 Figma의 100dp 상단 여백, 2단계는 안내 전체가 보이는 여백으로 조정한다.
                    val topPadding = ((maxHeight - 136.dp) / 2).coerceIn(0.dp, 100.dp)
                    SearchNoResultsContent(
                        modifier = Modifier.padding(top = topPadding, start = 20.dp, end = 20.dp),
                        message = "이 주변에는 아직 포토부스가 없어요.\n다른 곳을 검색해보세요!",
                    )
                }
                else -> LazyColumn(
                    state = listState,
                    modifier = listModifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp),
                ) {
                    items(photoBooths, key = { it.id }) { booth ->
                        HorizontalBrandItem(
                            photoBooth = booth,
                            modifier = Modifier.padding(vertical = 8.dp),
                            titleStyle = NekiTheme.typography.title18SemiBold,
                            onClickItem = { onBoothClick(booth) },
                            onClickFavorite = { onFavoriteClick(booth) },
                            extraInfo = if (showDistance) {
                                { DistanceInfo(booth.distance) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowSearchListButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .buttonShadow()
            .clip(CircleShape)
            .background(NekiTheme.colorScheme.white)
            .clickableSingle(onClick = onClick)
            .size(width = 97.dp, height = 40.dp)
            .padding(horizontal = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(R.drawable.icon_list), contentDescription = null, modifier = Modifier.size(16.dp), tint = NekiTheme.colorScheme.gray400)
        Text("목록보기", style = NekiTheme.typography.body14SemiBold, color = NekiTheme.colorScheme.gray800)
    }
}

@Composable
private fun BrandFilterChip(
    brandName: String?,
    extraCount: Int = 0,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clip(CircleShape)
            .clickableSingle(enabled = enabled, onClick = onClick)
            .border(width = 1.2.dp, color = NekiTheme.colorScheme.gray50, shape = CircleShape)
            .padding(start = 12.dp, end = 9.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                if (brandName == null) {
                    append("브랜드")
                } else {
                    withStyle(SpanStyle(color = NekiTheme.colorScheme.primary500)) {
                        append(brandName)
                    }
                    if (extraCount > 0) append(" 외 ${extraCount}개")
                }
            },
            style = NekiTheme.typography.body14SemiBold,
            color = NekiTheme.colorScheme.gray600,
        )
        Icon(
            painter = painterResource(R.drawable.icon_chip_down),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = NekiTheme.colorScheme.gray400,
        )
    }
}

private val PreviewBooths = persistentListOf(
    PhotoBooth(id = 1, brandName = "인생네컷", branchName = "사당역점", distance = 300),
    PhotoBooth(id = 2, brandName = "포토시그니처", branchName = "사당점", distance = 300),
    PhotoBooth(id = 3, brandName = "포토그레이", branchName = "사당역점", distance = 300),
)

@ComponentPreview
@Composable
private fun SearchResultFirstLevelPreview() {
    NekiTheme {
        SearchResultPhotoBoothPanel(
            photoBooths = PreviewBooths,
            dragLevel = DragLevel.FIRST,
            showDistance = true,
            isCurrentLocation = false,
            modifier = Modifier.size(375.dp, 760.dp),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchResultSecondLevelPreview() {
    NekiTheme {
        SearchResultPhotoBoothPanel(
            photoBooths = PreviewBooths,
            dragLevel = DragLevel.SECOND,
            showDistance = true,
            isCurrentLocation = true,
            modifier = Modifier.size(375.dp, 760.dp),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchResultThirdLevelPreview() {
    NekiTheme {
        SearchResultPhotoBoothPanel(
            photoBooths = PreviewBooths,
            dragLevel = DragLevel.THIRD,
            showDistance = true,
            isCurrentLocation = false,
            modifier = Modifier.size(375.dp, 760.dp),
        )
    }
}

@ComponentPreview
@Composable
private fun SearchResultEmptyPreview() {
    NekiTheme {
        SearchResultPanelContent(photoBooths = persistentListOf(), showDistance = false, modifier = Modifier.size(375.dp, SecondPanelHeight))
    }
}

@ComponentPreview
@Composable
private fun SearchResultEmptyExpandedPreview() {
    NekiTheme {
        SearchResultPanelContent(photoBooths = persistentListOf(), showDistance = false, modifier = Modifier.size(375.dp, 648.dp))
    }
}

@ComponentPreview
@Composable
private fun SearchResultLoadingPreview() {
    NekiTheme {
        SearchResultPanelContent(
            photoBooths = persistentListOf(),
            showDistance = false,
            modifier = Modifier.size(375.dp, SecondPanelHeight),
            isLoading = true,
        )
    }
}

@ComponentPreview
@Composable
private fun SearchResultErrorPreview() {
    NekiTheme {
        SearchResultPanelContent(
            photoBooths = persistentListOf(),
            showDistance = false,
            modifier = Modifier.size(375.dp, SecondPanelHeight),
            isError = true,
        )
    }
}

@ComponentPreview
@Composable
private fun ShowSearchListButtonPreview() {
    NekiTheme {
        Box(Modifier.padding(16.dp)) {
            ShowSearchListButton(onClick = {})
        }
    }
}

@ComponentPreview
@Composable
private fun BrandFilterChipStatesPreview() {
    NekiTheme {
        Column(Modifier.padding(16.dp)) {
            BrandFilterChip(brandName = null, enabled = true, onClick = {})
            BrandFilterChip(brandName = "인생네컷", enabled = true, onClick = {})
            BrandFilterChip(brandName = "포토시그니처", extraCount = 10, enabled = true, onClick = {})
            BrandFilterChip(brandName = null, enabled = false, onClick = {})
        }
    }
}
