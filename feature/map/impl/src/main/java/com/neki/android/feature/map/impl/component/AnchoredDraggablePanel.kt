package com.neki.android.feature.map.impl.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.modifier.dropdownShadow
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Brand
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.DragLevel
import com.neki.android.feature.map.impl.const.MapConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.roundToInt

@Composable
internal fun AnchoredDraggablePanel(
    brands: ImmutableList<Brand> = persistentListOf(),
    nearbyPhotoBooths: ImmutableList<PhotoBooth> = persistentListOf(),
    dragLevel: DragLevel = DragLevel.FIRST,
    isCurrentLocation: Boolean = false,
    onDragLevelChanged: (DragLevel) -> Unit = {},
    onClickCurrentLocation: () -> Unit = {},
    onClickInfoIcon: () -> Unit = {},
    onClickBrand: (Brand) -> Unit = {},
    onClickNearPhotoBooth: (PhotoBooth) -> Unit = {},
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val navigationBarHeightPx = with(density) {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx()
    }
    val bottomPanelHeightPx = with(density) {
        (MapConst.BOTTOM_NAVIGATION_BAR_HEIGHT +
            MapConst.PANEL_DRAG_LOCATION_HEIGHT +
            MapConst.PANEL_DRAG_LEVEL_FIRST_HEIGHT).dp.toPx() + navigationBarHeightPx
    }
    val centerPanelHeightPx = with(density) {
        (MapConst.BOTTOM_NAVIGATION_BAR_HEIGHT +
            MapConst.PANEL_DRAG_LOCATION_HEIGHT +
            MapConst.PANEL_DRAG_LEVEL_SECOND_HEIGHT).dp.toPx() + navigationBarHeightPx
    }
    var isProgrammaticTransition by remember { mutableStateOf(false) }

    val state = remember {
        val anchors = DraggableAnchors {
            DragLevel.FIRST at screenHeightPx - bottomPanelHeightPx
            DragLevel.SECOND at screenHeightPx - centerPanelHeightPx
            DragLevel.THIRD at screenHeightPx * 0.05f
            DragLevel.INVISIBLE at screenHeightPx
        }

        AnchoredDraggableState(
            initialValue = DragLevel.FIRST,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = splineBasedDecay(density),
            confirmValueChange = { newValue ->
                newValue != DragLevel.INVISIBLE || isProgrammaticTransition
            },
        )
    }

    LaunchedEffect(state.settledValue) {
        if (state.settledValue != dragLevel) {
            onDragLevelChanged(state.settledValue)
        }
    }

    LaunchedEffect(dragLevel) {
        isProgrammaticTransition = true
        state.animateTo(dragLevel)
        isProgrammaticTransition = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                val currentOffset = state.requireOffset()
                val shouldConstrainOffset = state.currentValue == DragLevel.FIRST && !isProgrammaticTransition
                val constrainedOffset = if (shouldConstrainOffset) {
                    currentOffset.coerceAtMost(state.anchors.positionOf(DragLevel.FIRST))
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
                    .padding(start = 20.dp, bottom = 8.dp)
                    .alpha(alpha = if (dragLevel == DragLevel.THIRD) 0f else 1f),
                isActiveCurrentLocation = isCurrentLocation,
                onClick = onClickCurrentLocation,
            )
            AnchoredPanelContent(
                brands = brands,
                nearbyPhotoBooths = nearbyPhotoBooths,
                onClickInfoIcon = onClickInfoIcon,
                onClickBrand = onClickBrand,
                onClickPhotoBooth = onClickNearPhotoBooth,
            )
        }
    }
}

@Composable
internal fun AnchoredPanelContent(
    brands: ImmutableList<Brand> = persistentListOf(),
    nearbyPhotoBooths: ImmutableList<PhotoBooth> = persistentListOf(),
    onClickInfoIcon: () -> Unit = {},
    onClickBrand: (Brand) -> Unit = {},
    onClickPhotoBooth: (PhotoBooth) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .dropdownShadow(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            BottomSheetDragHandle()
            Text(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                text = "네컷 사진 브랜드",
                color = NekiTheme.colorScheme.gray900,
                style = NekiTheme.typography.title18Bold,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(start = 20.dp),
            ) {
                items(brands) { brand ->
                    VerticalBrandItem(
                        brand = brand,
                        onClickItem = { onClickBrand(brand) },
                    )
                }
            }
        }
        VerticalSpacer(24.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = NekiTheme.colorScheme.primary400)) {
                            append("가까운")
                        }
                        append(" 네컷 사진 브랜드")
                    },
                    color = NekiTheme.colorScheme.gray900,
                    style = NekiTheme.typography.title18Bold,
                )
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_pin),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
            Icon(
                modifier = Modifier
                    .noRippleClickableSingle(onClick = onClickInfoIcon)
                    .padding(start = 2.dp, top = 2.dp, bottom = 2.dp)
                    .size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_info_stroked),
                contentDescription = null,
                tint = NekiTheme.colorScheme.gray300,
            )
        }
        VerticalSpacer(8.dp)
        val filteredNearbyPhotoBooths = nearbyPhotoBooths.filter { it.isCheckedBrand }
        if (filteredNearbyPhotoBooths.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 123.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.image_near_photo_booth_empty),
                    contentDescription = null,
                )
                Text(
                    text = "1km 이내에 가까운\n네컷 사진관이 없어요!",
                    color = NekiTheme.colorScheme.gray500,
                    style = NekiTheme.typography.body16Medium,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 128.dp),
            ) {
                items(filteredNearbyPhotoBooths) { photoBooth ->
                    HorizontalBrandItem(
                        photoBooth = photoBooth,
                        onClickItem = { onClickPhotoBooth(photoBooth) },
                    )
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun AnchoredPanelContentPreview() {
    NekiTheme {
        AnchoredPanelContent(
            brands = persistentListOf(
                Brand(isChecked = false, name = "인생네컷", imageUrl = "https://dev-yapp.suitestudy.com:4641/file/image/logo/LIFEFOURCUTS_LOGO_v1.png"),
                Brand(isChecked = false, name = "포토그레이", imageUrl = "https://dev-yapp.suitestudy.com:4641/file/image/logo/PHOTOGRAY_LOGO_v1.png"),
                Brand(isChecked = false, name = "포토이즘", imageUrl = "https://dev-yapp.suitestudy.com:4641/file/image/logo/PHOTOISM_LOGO_v1.png"),
                Brand(isChecked = false, name = "하루필름", imageUrl = "https://dev-yapp.suitestudy.com:4641/file/image/logo/HARUFILM_LOGO_v1.png"),
                Brand(
                    isChecked = false,
                    name = "플랜비\n스튜디오",
                    imageUrl = "https://dev-yapp.suitestudy.com:4641/file/image/logo/PLANB_STUDIO_LOGO_v1.png",
                ),
                Brand(
                    isChecked = false,
                    name = "포토시그니처",
                    imageUrl = "https://dev-yapp.suitestudy.com:4641/file/image/logo/PHOTOSIGNATURE_LOGO_v1.png",
                ),
            ),
            nearbyPhotoBooths = persistentListOf(
                PhotoBooth(
                    brandName = "인생네컷",
                    branchName = "가산디지털점",
                    distance = 25,
                    latitude = 37.5272,
                    longitude = 126.8864,
                ),
                PhotoBooth(
                    brandName = "포토그레이",
                    branchName = "가산역점",
                    distance = 38,
                    latitude = 37.5268,
                    longitude = 126.8867,
                ),
                PhotoBooth(
                    brandName = "포토이즘",
                    branchName = "마리오점",
                    distance = 52,
                    latitude = 37.5274,
                    longitude = 126.8858,
                ),
            ),
        )
    }
}
