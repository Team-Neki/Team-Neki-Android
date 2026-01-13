package com.neki.android.feature.pose.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_BOTTOM_PADDING
import com.neki.android.feature.pose.impl.const.PoseConst.POSE_LAYOUT_VERTICAL_SPACING
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun PoseListContent(
    topPadding: Dp,
    modifier: Modifier = Modifier,
    poseList: ImmutableList<String> = persistentListOf(),
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(2),
        state = state,
        contentPadding = PaddingValues(top = topPadding, bottom = POSE_LAYOUT_BOTTOM_PADDING.dp),
        verticalItemSpacing = POSE_LAYOUT_VERTICAL_SPACING.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(poseList) { imageUrl ->
            PoseItem(imageUrl = imageUrl)
        }
    }
}

@Composable
private fun PoseItem(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.FillWidth
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
