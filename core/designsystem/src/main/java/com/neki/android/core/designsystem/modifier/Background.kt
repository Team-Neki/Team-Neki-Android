package com.neki.android.core.designsystem.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
 * 포즈 컴포넌트에 적용되는 그라데이션 배경
 * 상단에서 134/242 지점까지 어두워지는 효과
 */
fun Modifier.poseBackground(
    shape: Shape = RoundedCornerShape(12.dp),
): Modifier = this.background(
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
 * @param alpha 블러 틴트에 적용될 색상의 알파 값
 * @param color 블러 효과에 적용될 배경 색상
 * @param blurRadius 블러 효과의 반경
 * @param enabled 블러 효과 활성화 여부 (false일 경우 단색 배경 적용)
 * @param defaultBackgroundColor 블러 비활성화 시 적용될 기본 배경 색상
 */
fun Modifier.backgroundHazeBlur(
    hazeState: HazeState,
    alpha: Float,
    color: Color,
    blurRadius: Dp,
    enabled: Boolean = true,
    defaultBackgroundColor: Color = color,
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
    )
