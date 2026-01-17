package com.neki.android.feature.map.impl.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.DragValue
import com.neki.android.feature.map.impl.const.FourCutBrand
import kotlin.math.roundToInt

//TODO: BottomNavigationBar 컴포넌트 디자인 변경사항에 맞춰 바꿔야 함
private val BOTTOM_NAV_BAR_HEIGHT = 72.dp

@Composable
fun AnchoredDraggablePanel(
    modifier: Modifier = Modifier,
    dragValue: DragValue = DragValue.Bottom,
    isCurrentLocation: Boolean = false,
    onDragValueChanged: (DragValue) -> Unit = {},
    onClickCurrentLocation: () -> Unit = {},
    onClickInfoIcon: () -> Unit = {},
    onClickBrand: (Boolean) -> Unit = {},
    onClickNearBrand: () -> Unit = {},
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }

    val navigationBarHeightPx = with(density) {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx()
    }

    val bottomNavBarHeightPx = with(density) {
        BOTTOM_NAV_BAR_HEIGHT.toPx()
    }

    // CurrentLocationButton: padding(8dp) * 2 + icon(20dp) = 36dp + bottom margin(12dp) = 48dp
    val currentLocationButtonHeightPx = with(density) { 48.dp.toPx() }

    var collapsedHeightPx by remember { mutableIntStateOf(0) }

    val state = remember(collapsedHeightPx) {
        val anchors = DraggableAnchors {
            DragValue.Bottom at if (collapsedHeightPx > 0) {
                screenHeightPx - collapsedHeightPx - bottomNavBarHeightPx - navigationBarHeightPx - currentLocationButtonHeightPx
            } else {
                screenHeightPx * 0.8f
            }
            DragValue.Center at screenHeightPx * 0.3f
            DragValue.Top at screenHeightPx * 0.05f
            DragValue.Invisible at screenHeightPx + navigationBarHeightPx
        }

        AnchoredDraggableState(
            initialValue = DragValue.Bottom,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(density),
            confirmValueChange = { newValue ->
                !(dragValue == DragValue.Bottom && newValue == DragValue.Invisible)
            }
        )
    }

    LaunchedEffect(state.settledValue) {
        if (state.settledValue != dragValue) {
            onDragValueChanged(state.settledValue)
        }
    }

    LaunchedEffect(dragValue) {
        state.animateTo(dragValue)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                IntOffset(0, state.requireOffset().roundToInt())
            }
            .anchoredDraggable(
                state = state,
                orientation = Orientation.Vertical,
            ),
    ) {
        Column {
            CurrentLocationButton(
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 12.dp)
                    .alpha(alpha = if (dragValue == DragValue.Top) 0f else 1f),
                isActiveCurrentLocation = isCurrentLocation,
                onClick = onClickCurrentLocation
            )
            AnchoredPanelContent(
                onCollapsedHeightMeasured = { collapsedHeightPx = it },
                onClickInfoIcon = onClickInfoIcon,
                onClickBrand = { onClickBrand(it) },
                onClickNearBrand = onClickNearBrand
            )
        }
    }
}

@Composable
fun AnchoredPanelContent(
    modifier: Modifier = Modifier,
    onCollapsedHeightMeasured: (Int) -> Unit = {},
    onClickInfoIcon: () -> Unit = {},
    onClickBrand: (Boolean) -> Unit = {},
    onClickNearBrand: () -> Unit = {},
) {
    val density = LocalDensity.current
    // "가까운 네컷 사진 브랜드" 텍스트 높이의 절반 + VerticalSpacer(24.dp)
    val additionalHeightPx = with(density) { (24.dp + 12.dp).toPx().toInt() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onCollapsedHeightMeasured(coordinates.size.height + additionalHeightPx)
                }
        ) {
            BottomSheetDragHandle()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = "네컷 사진 브랜드",
                    color = NekiTheme.colorScheme.gray900,
                    style = NekiTheme.typography.title18Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(FourCutBrand.entries) { brand ->
                        VerticalBrandItem(
                            brand,
                            onItemClick = { onClickBrand(it) }
                        )
                    }
                }
            }
        }
        VerticalSpacer(24.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "가까운 네컷 사진 브랜드 \uD83D\uDCCC",
                color = NekiTheme.colorScheme.gray900,
                style = NekiTheme.typography.title18Bold
            )
            AsyncImage(
                modifier = Modifier
                    .size(16.dp)
                    .noRippleClickableSingle(onClick = onClickInfoIcon),
                model = R.drawable.icon_info_gray_stroke,
                contentDescription = null
            )
        }
        VerticalSpacer(8.dp)
        LazyColumn(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(FourCutBrand.entries) { brand ->
                HorizontalBrandItem(
                    brand,
                    onItemClick = onClickNearBrand
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun AnchoredDraggablePanelPreview() {
    NekiTheme {
        AnchoredDraggablePanel()
    }
}

@ComponentPreview
@Composable
private fun AnchoredPanelContentPreview() {
    NekiTheme {
        AnchoredPanelContent()
    }
}
