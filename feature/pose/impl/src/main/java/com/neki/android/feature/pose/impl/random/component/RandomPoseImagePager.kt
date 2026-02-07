package com.neki.android.feature.pose.impl.random.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Pose
import com.neki.android.feature.pose.impl.const.PoseConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun RandomPoseImagePager(
    pagerState: PagerState,
    poseList: ImmutableList<Pose>,
    modifier: Modifier = Modifier,
    onLeftSwipe: () -> Unit = {},
    onRightSwipe: () -> Unit = {},
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        beyondViewportPageCount = PoseConst.POSE_PREFETCH_THRESHOLD,
        userScrollEnabled = true,
    ) { index ->
        RandomPoseImage(
            pose = poseList[index],
            onLeftSwipe = onLeftSwipe,
            onRightSwipe = onRightSwipe,
        )
    }
}

@Composable
private fun RandomPoseImage(
    pose: Pose,
    modifier: Modifier = Modifier,
    onLeftSwipe: () -> Unit = {},
    onRightSwipe: () -> Unit = {},
) {
    Box(
        modifier = modifier.padding(horizontal = 10.dp),
    ) {
        AsyncImage(
            model = pose.poseImageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(NekiTheme.colorScheme.white),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.Center,
        )
        Row(
            modifier = Modifier.matchParentSize(),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .noRippleClickableSingle(onClick = onLeftSwipe),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .noRippleClickableSingle(onClick = onRightSwipe),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RandomPoseImagePagerPreview() {
    val poseList = persistentListOf(
        Pose(id = 1L, poseImageUrl = "https://example.com/pose1.jpg"),
        Pose(id = 2L, poseImageUrl = "https://example.com/pose2.jpg"),
    )
    RandomPoseImagePager(
        pagerState = rememberPagerState { poseList.size },
        poseList = poseList,
    )
}

@Preview(showBackground = true)
@Composable
private fun RandomPoseImagePreview() {
    RandomPoseImage(
        pose = Pose(id = 1L, poseImageUrl = "https://example.com/pose.jpg"),
    )
}
