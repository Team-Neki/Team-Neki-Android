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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
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
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Brand
import com.neki.android.core.model.BrandInfo
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.DragValue
import com.neki.android.feature.map.impl.const.MapConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.roundToInt

@Composable
fun AnchoredDraggablePanel(
    brands: ImmutableList<Brand> = persistentListOf(),
    nearbyBrands: ImmutableList<BrandInfo> = persistentListOf(),
    dragValue: DragValue = DragValue.Bottom,
    isCurrentLocation: Boolean = false,
    onDragValueChanged: (DragValue) -> Unit = {},
    onClickCurrentLocation: () -> Unit = {},
    onClickInfoIcon: () -> Unit = {},
    onClickBrand: (Boolean) -> Unit = {},
    onClickNearBrand: (BrandInfo) -> Unit = {},
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    var collapsedHeightPx by remember { mutableIntStateOf(0) }
    val currentLocationButtonHeightPx = with(density) { 48.dp.toPx() }  // 36.dp + 12.dp
    val bottomNavBarHeightPx = with(density) { MapConst.BOTTOM_NAVIGATION_BAR_HEIGHT.dp.toPx() }
    val navigationBarHeightPx = with(density) { WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx() }

    var isProgrammaticTransition by remember { mutableStateOf(false) }

    val state = remember(collapsedHeightPx) {
        val anchors = DraggableAnchors {
            DragValue.Bottom at screenHeightPx - collapsedHeightPx - bottomNavBarHeightPx - currentLocationButtonHeightPx - navigationBarHeightPx
            DragValue.Center at screenHeightPx * 0.3f
            DragValue.Top at screenHeightPx * 0.05f
            DragValue.Invisible at screenHeightPx
        }

        AnchoredDraggableState(
            initialValue = DragValue.Bottom,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(density),
            confirmValueChange = { newValue ->
                newValue != DragValue.Invisible || isProgrammaticTransition
            }
        )
    }

    LaunchedEffect(state.settledValue) {
        if (state.settledValue != dragValue) {
            onDragValueChanged(state.settledValue)
        }
    }

    LaunchedEffect(dragValue) {
        isProgrammaticTransition = true
        state.animateTo(dragValue)
        isProgrammaticTransition = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                val currentOffset = state.requireOffset()
                val shouldConstrainOffset = state.currentValue == DragValue.Bottom && !isProgrammaticTransition
                val constrainedOffset = if (shouldConstrainOffset) {
                    currentOffset.coerceAtMost(state.anchors.positionOf(DragValue.Bottom))
                } else {
                    currentOffset
                }
                IntOffset(0, constrainedOffset.roundToInt())
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
                brands = brands,
                nearbyBrands = nearbyBrands,
                dragValue = dragValue,
                onCollapsedHeightMeasured = { collapsedHeightPx = it },
                onClickInfoIcon = onClickInfoIcon,
                onClickBrand = onClickBrand,
                onClickNearBrand = onClickNearBrand
            )
        }
    }
}

@Composable
fun AnchoredPanelContent(
    modifier: Modifier = Modifier,
    brands: ImmutableList<Brand> = persistentListOf(),
    nearbyBrands: ImmutableList<BrandInfo> = persistentListOf(),
    dragValue: DragValue = DragValue.Bottom,
    onCollapsedHeightMeasured: (Int) -> Unit = {},
    onClickInfoIcon: () -> Unit = {},
    onClickBrand: (Boolean) -> Unit = {},
    onClickNearBrand: (BrandInfo) -> Unit = {},
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp

    /** 패널 외부 상단 현위치 버튼 영역 **/
    val additionalHeightPx = with(density) { (24.dp + 12.dp).toPx().toInt() }

    val extraBottomPadding = when (dragValue) {
        DragValue.Center -> screenHeightDp * 0.3f
        DragValue.Top -> screenHeightDp * 0.05f
        else -> 0.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .buttonShadow(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                offsetY = 0.dp,
                blurRadius = 5.dp
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    val newHeight = it.size.height + additionalHeightPx
                    onCollapsedHeightMeasured(newHeight)
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
                    items(brands) { brand ->
                        VerticalBrandItem(
                            brand = brand,
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
                    .size(24.dp)
                    .noRippleClickableSingle(onClick = onClickInfoIcon)
                    .padding(2.dp),
                model = R.drawable.icon_info_gray_stroke,
                contentDescription = null
            )
        }
        VerticalSpacer(8.dp)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = extraBottomPadding)
        ) {
            items(nearbyBrands) { brandInfo ->
                HorizontalBrandItem(
                    brandInfo = brandInfo,
                    onItemClick = { onClickNearBrand(brandInfo) }
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun AnchoredPanelContentPreview() {
    NekiTheme {
        AnchoredPanelContent()
    }
}
