package com.neki.android.feature.photo_upload.impl.qrscan.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRScanConst.FRAME_CORNER_LENGTH
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRScanConst.FRAME_CORNER_RADIUS
import com.neki.android.feature.photo_upload.impl.qrscan.const.QRScanConst.FRAME_STROKE_WIDTH

@Composable
internal fun ScanCornerFrame(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    strokeWidth: Dp = FRAME_STROKE_WIDTH.dp,
    cornerRadius: Dp = FRAME_CORNER_RADIUS.dp,
    cornerLength: Dp = FRAME_CORNER_LENGTH.dp,
) {
    Canvas(modifier = modifier) {
        val sw = strokeWidth.toPx()
        val r = cornerRadius.toPx()
        val len = cornerLength.toPx()
        val m = (-strokeWidth / 2).toPx()

        fun drawCornerTL() {
            val p = Path().apply {
                moveTo(m, m + len)
                lineTo(m, m + r)
                quadraticTo(m, m, m + r, m)
                lineTo(m + len, m)
            }
            drawPath(
                path = p,
                color = color,
                style = Stroke(width = sw, cap = StrokeCap.Square, join = StrokeJoin.Round),
            )
        }

        fun drawCornerTR() {
            val w = size.width
            val p = Path().apply {
                moveTo(w - m, m + len)
                lineTo(w - m, m + r)
                quadraticTo(w - m, m, w - m - r, m)
                lineTo(w - m - len, m)
            }
            drawPath(
                path = p,
                color = color,
                style = Stroke(sw, cap = StrokeCap.Square, join = StrokeJoin.Round),
            )
        }

        fun drawCornerBL() {
            val h = size.height
            val p = Path().apply {
                moveTo(m, h - m - len)
                lineTo(m, h - m - r)
                quadraticTo(m, h - m, m + r, h - m)
                lineTo(m + len, h - m)
            }
            drawPath(
                path = p,
                color = color,
                style = Stroke(sw, cap = StrokeCap.Square, join = StrokeJoin.Round),
            )
        }

        fun drawCornerBR() {
            val w = size.width
            val h = size.height
            val p = Path().apply {
                moveTo(w - m, h - m - len)
                lineTo(w - m, h - m - r)
                quadraticTo(w - m, h - m, w - m - r, h - m)
                lineTo(w - m - len, h - m)
            }
            drawPath(
                path = p,
                color = color,
                style = Stroke(sw, cap = StrokeCap.Square, join = StrokeJoin.Round),
            )
        }

        drawCornerTL()
        drawCornerTR()
        drawCornerBL()
        drawCornerBR()
    }
}

@Preview
@Composable
private fun ScanCornerFramePreview() {
    NekiTheme {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(340.dp)
                .background(Color(0xFF5A5A66)),
        ) {
            ScanCornerFrame(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
