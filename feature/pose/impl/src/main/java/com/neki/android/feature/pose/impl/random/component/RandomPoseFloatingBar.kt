package com.neki.android.feature.pose.impl.random.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.pose.impl.R
import com.neki.android.core.designsystem.R as DesignR

@Composable
internal fun RandomPoseFloatingBarContent(
    modifier: Modifier = Modifier,
    isScrapped: Boolean = false,
    onClickClose: () -> Unit = {},
    onClickGoToDetail: () -> Unit = {},
    onClickScrap: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF5647).copy(alpha = 0f),
                        Color(0xFFFF5647),
                    ),
                ),
                alpha = 0.24f,
            )
            .padding(top = 38.dp, bottom = 34.dp),
    ) {
        Row(
            modifier = modifier
                .clip(CircleShape)
                .background(
                    color = NekiTheme.colorScheme.white.copy(alpha = 0.6f),
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NekiTheme.colorScheme.white.copy(alpha = 0.2f),
                            NekiTheme.colorScheme.white,
                        ),
                    ),
                    shape = CircleShape,
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RandomPoseButton(
                onClick = onClickClose,
                backgroundColor = Color(0xFFF9FAFA).copy(alpha = 0.9f),
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(DesignR.drawable.icon_close),
                    contentDescription = "닫기",
                    tint = NekiTheme.colorScheme.gray800,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RandomPoseButton(
                    onClick = onClickGoToDetail,
                    backgroundColor = NekiTheme.colorScheme.primary400,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_top_right),
                        contentDescription = "상세 보기",
                        tint = NekiTheme.colorScheme.white,
                    )
                }

                RandomPoseButton(
                    onClick = onClickScrap,
                    backgroundColor = NekiTheme.colorScheme.primary400,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(
                            if (isScrapped) R.drawable.ic_scrap_selected
                            else R.drawable.icon_scrap_unselected,
                        ),
                        contentDescription = "스크랩",
                        tint = NekiTheme.colorScheme.white,
                    )
                }
            }
        }
    }
}

@Composable
private fun RandomPoseButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    NekiIconButton(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        onClick = onClick,
    ) {
        content()
    }
}

@ComponentPreview
@Composable
private fun RandomPoseFloatingBarContentPreview() {
    NekiTheme {
        RandomPoseFloatingBarContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
    }
}

@ComponentPreview
@Composable
private fun RandomPoseFloatingBarContentScrappedPreview() {
    NekiTheme {
        RandomPoseFloatingBarContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            isScrapped = true,
        )
    }
}
