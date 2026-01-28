package com.neki.android.core.designsystem.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

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

/**
 * 블러 효과가 적용된 배경을 설정하는 Modifier 확장 함수
 *
 * @param hazeState Haze 블러 효과를 관리하는 상태 객체
 * @param enabled 블러 효과 활성화 여부 (false일 경우 단색 배경 적용)
 * @param color 블러 효과에 적용될 배경 색상
 * @param defaultBackgroundColor 블러 비활성화 시 적용될 기본 배경 색상
 * @param blurRadius 블러 효과의 반경
 */
@Composable
fun Modifier.backgroundHazeBlur(
    hazeState: HazeState,
    enabled: Boolean = true,
    color: Color = Color(0xFF202227).copy(alpha = 0.9f),
    defaultBackgroundColor: Color = color,
    blurRadius: Dp = 12.dp,
): Modifier =
    if (enabled) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                backgroundColor = color,
                tint = HazeTint(
                    color.copy(alpha = if (color.luminance() >= 0.5) 0.6f else 0.65f),
                ),
                blurRadius = blurRadius,
            ),
        )
    } else this.background(color = defaultBackgroundColor)
