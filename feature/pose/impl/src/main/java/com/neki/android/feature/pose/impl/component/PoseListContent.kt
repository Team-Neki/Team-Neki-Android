package com.neki.android.feature.pose.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Pose
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_VERTICAL_SPACING
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun PoseListContent(
    topPadding: Dp,
    modifier: Modifier = Modifier,
    poseList: ImmutableList<Pose> = persistentListOf(),
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
        items(poseList) { pose ->
            PoseItem(
                pose = pose,
                onClickItem = onClickItem,
            )
        }
    }
}

@Composable
private fun PoseItem(
    pose: Pose,
    modifier: Modifier = Modifier,
    onClickItem: (Pose) -> Unit = {},
) {
    AsyncImage(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .noRippleClickable { onClickItem(pose) },
        model = pose.poseImageUrl,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}

@ComponentPreview
@Composable
private fun PoseListContentPreview() {
    NekiTheme {
        PoseListContent(
            topPadding = 0.dp,
            poseList = persistentListOf(),
        )
    }
}
