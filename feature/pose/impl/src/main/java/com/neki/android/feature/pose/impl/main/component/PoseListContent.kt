package com.neki.android.feature.pose.impl.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Pose
import com.neki.android.core.ui.component.GridItemOverlay
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
    var aspectRatio by rememberSaveable { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .noRippleClickable { onClickItem(pose) },
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (aspectRatio > 0f) Modifier.aspectRatio(aspectRatio)
                    else Modifier,
                ),
            model = pose.poseImageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            onSuccess = { state ->
                val size = state.painter.intrinsicSize
                if (size.width > 0f && size.height > 0f) {
                    aspectRatio = size.width / size.height
                }
            },
        )
        GridItemOverlay(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(12.dp),
        )
        if (pose.isScrapped) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_scrap_filled),
                contentDescription = null,
                tint = NekiTheme.colorScheme.white,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun PoseItemPreview() {
    NekiTheme {
        PoseItem(
            pose = Pose(
                id = 1,
                poseImageUrl = "",
            ),
        )
    }
}
