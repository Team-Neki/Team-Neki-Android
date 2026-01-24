package com.neki.android.feature.photo_upload.impl.qrscan.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRLayoutConst.CUTOUT_RADIUS
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRLayoutConst.DIM_COLOR

@Composable
internal fun DimExceptContent(
    offSet: Offset?,
    size: IntSize?,
    modifier: Modifier = Modifier,
    dimColor: Color = Color(DIM_COLOR),
    cutoutRadius: Dp = CUTOUT_RADIUS.dp,
) {
    val density = LocalDensity.current

    val radiusPx = with(density) { cutoutRadius.toPx() }

    Box(
        modifier = modifier,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            drawRect(dimColor)

            if (offSet == null || size == null) return@Canvas

            if (radiusPx > 0f) {
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = offSet,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    cornerRadius = CornerRadius(radiusPx, radiusPx),
                    blendMode = BlendMode.Clear,
                )
            } else {
                drawRect(
                    color = Color.Transparent,
                    topLeft = offSet,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    blendMode = BlendMode.Clear,
                )
            }
        }
    }
}
