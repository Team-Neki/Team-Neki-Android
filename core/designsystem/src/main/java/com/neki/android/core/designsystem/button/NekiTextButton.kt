package com.neki.android.core.designsystem.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.MultipleEventsCutter
import com.neki.android.core.designsystem.modifier.get
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun NekiTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    multipleEventsCutterEnabled: Boolean = true,
    enabledTextColor: Color = NekiTheme.colorScheme.primary500,
    disabledTextColor: Color = NekiTheme.colorScheme.gray200,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable () -> Unit = {},
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    TextButton(
        onClick = {
            if (multipleEventsCutterEnabled) {
                multipleEventsCutter.processEvent { onClick() }
            } else {
                onClick()
            }
        },
        modifier = modifier,
        contentPadding = contentPadding,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = enabledTextColor,
            disabledContentColor = disabledTextColor,
        ),
    ) {
        content()
    }
}

@ComponentPreview
@Composable
private fun NekiTextButtonPreview() {
    NekiTheme {
        NekiTextButton(
            onClick = {},
        ) {
            Text(
                text = "텍스트버튼",
            )
        }
    }
}
