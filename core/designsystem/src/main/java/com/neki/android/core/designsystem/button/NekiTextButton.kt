package com.neki.android.core.designsystem.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

/**
 * 텍스트 전용 버튼 컴포넌트.
 * Box + clickableSingle 기반으로, 500ms 중복 클릭 방지와 리플 효과를 포함한다.
 *
 * @param onClick 클릭 이벤트 핸들러
 * @param modifier 이 버튼에 적용할 [Modifier]
 * @param enabled 버튼 활성화 여부
 * @param shape 버튼의 클리핑 및 테두리 모양. 기본값은 [RectangleShape]
 * @param contentColor 활성 상태의 콘텐츠 색상. [Color.Unspecified]이면 content에서 직접 지정
 * @param disabledContentColor 비활성 상태의 콘텐츠 색상
 * @param border 테두리. null이면 테두리 없음
 * @param contentPadding 버튼 내부 패딩. 기본값 horizontal 20.dp, vertical 15.dp
 * @param content 버튼 내부 콘텐츠. 일반적으로 [Text]
 */
@Composable
fun NekiTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    contentColor: Color = Color.Unspecified,
    disabledContentColor: Color = Color.Unspecified,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
    content: @Composable () -> Unit = {},
) {
    val resolvedContentColor = if (enabled) contentColor else disabledContentColor

    Box(
        modifier = modifier
            .clip(shape)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clickableSingle(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        if (resolvedContentColor != Color.Unspecified) {
            CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
                content()
            }
        } else {
            content()
        }
    }
}

@ComponentPreview
@Composable
private fun NekiTextButtonPreview() {
    NekiTheme {
        NekiTextButton(onClick = {}) {
            Text(
                text = "텍스트버튼",
                color = NekiTheme.colorScheme.primary500,
            )
        }
    }
}
