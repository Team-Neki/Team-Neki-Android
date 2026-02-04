package com.neki.android.feature.pose.impl.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.RectangleShape
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.modifier.poseBackground
import com.neki.android.core.model.Pose
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_VERTICAL_SPACING

@Composable
internal fun PoseListContent(
    topPadding: Dp,
    modifier: Modifier = Modifier,
    posePagingItems: LazyPagingItems<Pose>,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onClickItem: (Pose) -> Unit = {},
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        columns = StaggeredGridCells.Fixed(2),
        state = state,
        contentPadding = PaddingValues(top = topPadding, bottom = POSE_LAYOUT_BOTTOM_PADDING.dp),
        verticalItemSpacing = POSE_LAYOUT_VERTICAL_SPACING.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = posePagingItems.itemCount,
            key = posePagingItems.itemKey { it.id },
        ) { index ->
            posePagingItems[index]?.let { pose ->
                PoseItem(
                    pose = pose,
                    onClickItem = onClickItem,
                )
            }
        }
    }
}

@Composable
private fun PoseItem(
    pose: Pose,
    modifier: Modifier = Modifier,
    onClickItem: (Pose) -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .noRippleClickable { onClickItem(pose) },
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = pose.poseImageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .poseBackground(shape = RectangleShape),
        )
    }
}
