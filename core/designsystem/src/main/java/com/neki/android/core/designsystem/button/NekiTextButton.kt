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
