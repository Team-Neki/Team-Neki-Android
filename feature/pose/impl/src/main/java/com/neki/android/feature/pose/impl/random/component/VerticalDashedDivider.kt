package com.neki.android.feature.pose.impl.random.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun VerticalDashedDivider(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.dp,
    dashOn: Dp = 5.dp,
    dashOff: Dp = 5.dp,
) {
    val white = NekiTheme.colorScheme.white
    Canvas(modifier = modifier.width(strokeWidth)) {
        val w = strokeWidth.toPx()

        val dash = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashOn.toPx(), dashOff.toPx()),
            phase = 0f,
        )

        val brush = linearGradient(
            colorStops = arrayOf(
                0f to white.copy(alpha = 0f),
                0.5f to white.copy(alpha = 1f),
                1f to white.copy(alpha = 0f),
            ),
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
        )

        drawLine(
            brush = brush,
            start = Offset(size.width / 2f, 0f),
            end = Offset(size.width / 2f, size.height),
            strokeWidth = w,
            pathEffect = dash,
        )
    }
}

@ComponentPreview
@Composable
private fun VerticalDashedDividerPreview() {
    NekiTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NekiTheme.colorScheme.gray900.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center,
        ) {
            VerticalDashedDivider(
                modifier = Modifier.fillMaxHeight(0.8f),
            )
        }
    }
}
