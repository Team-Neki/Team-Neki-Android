package com.neki.android.core.designsystem.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 사진 컴포넌트에 적용되는 그라데이션 배경
 * 좌하단에서 우상단으로 갈수록 어두워지는 효과
 */
fun Modifier.photoBackground(
    shape: Shape = RoundedCornerShape(12.dp),
): Modifier = this.background(
    brush = Brush.linearGradient(
        colorStops = arrayOf(
            0f to Color.Black.copy(alpha = 0f),
            0.7f to Color.Black.copy(alpha = 0.09f),
            1f to Color.Black.copy(alpha = 0.3f),
        ),
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(Float.POSITIVE_INFINITY, 0f),
    ),
    shape = shape,
)
