package com.neki.android.core.designsystem.modifier

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Figma btn_shadow 스타일
 * DROP_SHADOW: color #00000040, offset (0, 2ㅌㅌ), radius 8, spread 0
 */
fun Modifier.buttonShadow(
    shape: Shape = CircleShape,
    color: Color = Color.Black.copy(alpha = 0.25f),
    offsetX: Dp = 0.dp,
    offsetY: Dp = 2.dp,
    blurRadius: Dp = 8.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = Color.Transparent.toArgb()
                setShadowLayer(
                    blurRadius.toPx(),
                    offsetX.toPx(),
                    offsetY.toPx(),
                    color.toArgb(),
                )
            }
        }
        val outline = shape.createOutline(size, layoutDirection, this)
        canvas.drawOutline(outline, paint)
    }
}

/**
 * Figma card_shadow 스타일
 * DROP_SHADOW: color #00000040, offset (0, 2), radius 4, spread 0
 */
fun Modifier.cardShadow(
    shape: Shape = RectangleShape,
    color: Color = Color.Black.copy(alpha = 0.25f),
    offsetX: Dp = 0.dp,
    offsetY: Dp = 2.dp,
    blurRadius: Dp = 4.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = Color.Transparent.toArgb()
                setShadowLayer(
                    blurRadius.toPx(),
                    offsetX.toPx(),
                    offsetY.toPx(),
                    color.toArgb(),
                )
            }
        }
        val outline = shape.createOutline(size, layoutDirection, this)
        canvas.drawOutline(outline, paint)
    }
}

/**
 * Figma tabbar_shadow 스타일
 * DROP_SHADOW: color #0000000A, offset (0, -2), radius 10, spread 0
 */
fun Modifier.tabbarShadow(
    shape: Shape = RectangleShape,
    color: Color = Color.Black.copy(alpha = 0.04f),
    offsetX: Dp = 0.dp,
    offsetY: Dp = (-2).dp,
    blurRadius: Dp = 10.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = Color.Transparent.toArgb()
                setShadowLayer(
                    blurRadius.toPx(),
                    offsetX.toPx(),
                    offsetY.toPx(),
                    color.toArgb(),
                )
            }
        }
        val outline = shape.createOutline(size, layoutDirection, this)
        canvas.drawOutline(outline, paint)
    }
}

/**
 * Figma dropdown_shadow 스타일
 * DROP_SHADOW: color #00000033, offset (0, 0), radius 5, spread 0
 */
fun Modifier.dropdownShadow(
    shape: Shape = RectangleShape,
    color: Color = Color.Black.copy(alpha = 0.20f),
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blurRadius: Dp = 5.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = Color.Transparent.toArgb()
                setShadowLayer(
                    blurRadius.toPx(),
                    offsetX.toPx(),
                    offsetY.toPx(),
                    color.toArgb(),
                )
            }
        }
        val outline = shape.createOutline(size, layoutDirection, this)
        canvas.drawOutline(outline, paint)
    }
}
