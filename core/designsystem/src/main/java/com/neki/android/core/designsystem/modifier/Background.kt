package com.neki.android.core.designsystem.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

/**
 * 사진, 포즈 컴포넌트에 적용되는 그라데이션 배경
 */
fun Modifier.photoGridBackground(
    shape: Shape = RoundedCornerShape(8.dp),
): Modifier = this
    .background(
        color = Color.Black.copy(alpha = 0.04f),
        shape = shape,
    )
    .background(
        brush = Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.Black.copy(alpha = 0.2f),
                134f / 242f to Color.Black.copy(alpha = 0f),
            ),
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
fun Modifier.backgroundHazeBlur(
    hazeState: HazeState,
    alpha: Float,
    color: Color,
    blurRadius: Dp,
    enabled: Boolean = true,
    defaultBackgroundColor: Color = color,
    shape: Shape = RectangleShape,
): Modifier =
    if (enabled) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                backgroundColor = color,
                tint = HazeTint(color.copy(alpha = alpha)),
                blurRadius = blurRadius,
            ),
        )
    } else this.background(
        color = defaultBackgroundColor,
        shape = shape,
    )
