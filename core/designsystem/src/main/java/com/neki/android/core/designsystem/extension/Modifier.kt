package com.neki.android.core.designsystem.extension

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 클릭의 리플 효과를 없애주는 [Modifier]
 * */
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}

/**
 * 클릭의 리플 효과를 없애고 500L 내에 중복 클릭을를 막아주는 [Modifier]
 * */
fun Modifier.noRippleClickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "noRippleClickableSingle"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    },
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { multipleEventsCutter.processEvent { onClick() } },
        role = role,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    )
}

/**
 * 500L 내의 중복 클릭을 막아주는 [Modifier]
 * */
fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickableSingle"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    },
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { multipleEventsCutter.processEvent { onClick() } },
        role = role,
        indication = ripple(),
        interactionSource = remember { MutableInteractionSource() },
    )
}

internal interface MultipleEventsCutter {
    fun processEvent(event: () -> Unit)

    companion object
}

internal fun MultipleEventsCutter.Companion.get(): MultipleEventsCutter =
    MultipleEventsCutterImpl()

private class MultipleEventsCutterImpl : MultipleEventsCutter {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= 500L) {
            lastEventTimeMs = now
            event.invoke()
        }
    }
}

/**
 * Figma btn_shadow 스타일
 * DROP_SHADOW: color #00000040, offset (0, 2), radius 8, spread 0
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
    shape: Shape,
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
 * 사진 컴포넌트에 적용되는 그라데이션 배경
 * 좌하단에서 우상단으로 갈수록 어두워지는 효과
 */
fun Modifier.photoBackground(): Modifier = this.background(
    brush = Brush.linearGradient(
        colorStops = arrayOf(
            0f to Color.Black.copy(alpha = 0f),
            0.7f to Color.Black.copy(alpha = 0.09f),
            1f to Color.Black.copy(alpha = 0.3f),
        ),
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(Float.POSITIVE_INFINITY, 0f),
    ),
)
