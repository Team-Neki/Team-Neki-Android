package com.neki.android.feature.pose.impl.random.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.backgroundHazeBlur
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.HorizontalSpacer
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.pose.impl.R
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun RandomPoseTutorialOverlay(
    onClickStart: () -> Unit,
    hazeState: HazeState = rememberHazeState(),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .backgroundHazeBlur(hazeState)
                .fillMaxSize()
                .padding(top = 60.dp, bottom = 34.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                HorizontalSpacer(40f)
                TutorialGuideItem(
                    iconRes = R.drawable.icon_seek_before_pose,
                    label = "이전 포즈",
                )
                HorizontalSpacer(45f)
                VerticalDashedDivider(
                    modifier = Modifier.fillMaxHeight(),
                )
                HorizontalSpacer(45f)
                TutorialGuideItem(
                    iconRes = R.drawable.icon_seek_after_pose,
                    label = "다음 포즈",
                )
                HorizontalSpacer(40f)
            }
            VerticalSpacer(20.dp)

            StartRandomPoseButton(
                onClick = onClickStart,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF5647).copy(alpha = 0f),
                            Color(0xFFFF5647),
                        ),
                        startY = Float.POSITIVE_INFINITY,
                        endY = 0f,
                    ),
                    alpha = 0.24f,
                ),
        )
    }
}

@Composable
private fun TutorialGuideItem(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpacer(275f)
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Unspecified,
        )
        VerticalSpacer(6.dp)
        Text(
            text = label,
            style = NekiTheme.typography.body16SemiBold,
            color = NekiTheme.colorScheme.white,
        )
        VerticalSpacer(243f)
    }
}

@ComponentPreview
@Composable
private fun RandomPoseTutorialOverlayPreview() {
    NekiTheme {
        val state = rememberHazeState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state),
        )
        RandomPoseTutorialOverlay(
            hazeState = state,
            onClickStart = {},
        )
    }
}
