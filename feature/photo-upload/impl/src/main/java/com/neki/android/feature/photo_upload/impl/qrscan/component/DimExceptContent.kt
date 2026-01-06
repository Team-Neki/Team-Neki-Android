package com.neki.android.feature.photo_upload.impl.qrscan.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRLayoutConst.CUTOUT_RADIUS
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRLayoutConst.DIM_COLOR

@Immutable
private data class CutoutRectPx(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
)

@Composable
fun DimExceptContent(
    modifier: Modifier = Modifier,
    dimColor: Color = Color(DIM_COLOR),
    cutoutRadius: Dp = CUTOUT_RADIUS.dp,
    cutoutPadding: Dp = 0.dp,
    cutout: @Composable BoxScope.(Modifier) -> Unit,
    foreground: @Composable BoxScope.() -> Unit = {},
) {
    val density = LocalDensity.current
    var cutoutRect by remember { mutableStateOf<CutoutRectPx?>(null) }

    val paddingPx = with(density) { cutoutPadding.toPx() }
    val radiusPx = with(density) { cutoutRadius.toPx() }

    Box(modifier = modifier.fillMaxSize()) {
        cutout(
            Modifier.onGloballyPositioned { coords ->
                val pos = coords.positionInParent()
                val size = coords.size
                cutoutRect = CutoutRectPx(
                    left = pos.x,
                    top = pos.y,
                    width = size.width.toFloat(),
                    height = size.height.toFloat(),
                )
            },
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            drawRect(dimColor)

            val c = cutoutRect ?: return@Canvas

            val left = c.left - paddingPx
            val top = c.top - paddingPx
            val w = c.width + paddingPx * 2f
            val h = c.height + paddingPx * 2f

            if (radiusPx > 0f) {
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(radiusPx, radiusPx),
                    blendMode = BlendMode.Clear,
                )
            } else {
                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    blendMode = BlendMode.Clear,
                )
            }
        }

        foreground()
    }
}
