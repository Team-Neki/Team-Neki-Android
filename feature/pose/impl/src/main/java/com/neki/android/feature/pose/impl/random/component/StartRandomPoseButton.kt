package com.neki.android.feature.pose.impl.random.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun StartRandomPoseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0f to NekiTheme.colorScheme.primary400,
                        0.53f to NekiTheme.colorScheme.primary600,
                        0.96f to Color(0xFFFF334B),
                    ),
                ),
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NekiTheme.colorScheme.primary300,
                        NekiTheme.colorScheme.primary500,
                    ),
                ),
                shape = RoundedCornerShape(12.dp),
                width = 1.dp,
            )
            .clickableSingle(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "랜덤 포즈 시작하기",
            style = NekiTheme.typography.body16SemiBold,
            color = NekiTheme.colorScheme.white,
        )
    }
}

@ComponentPreview
@Composable
private fun StartRandomPoseButtonPreview() {
    NekiTheme {
        StartRandomPoseButton(
            onClick = {},
        )
    }
}
