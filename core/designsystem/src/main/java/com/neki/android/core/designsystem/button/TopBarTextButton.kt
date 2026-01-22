package com.neki.android.core.designsystem.button

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun TopBarTextButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enabledTextColor: Color = NekiTheme.colorScheme.primary500,
    disabledTextColor: Color = NekiTheme.colorScheme.gray200,
    onClick: () -> Unit = {},
) {
    NekiTextButton(
        modifier = modifier.offset(
            x = ButtonDefaults.TextButtonContentPadding.calculateLeftPadding(LayoutDirection.Ltr),
        ),
        onClick = onClick,
    ) {
        Text(
            text = buttonText,
            style = NekiTheme.typography.body16SemiBold,
            color = if (enabled) enabledTextColor else disabledTextColor,
        )
    }
}

@ComponentPreview
@Composable
private fun EnabledTopBarTextButtonPreview() {
    NekiTheme {
        TopBarTextButton(
            buttonText = "텍스트버튼",
            onClick = {},
        )
    }
}

@ComponentPreview
@Composable
private fun DisabledTopBarTextButtonPreview() {
    NekiTheme {
        TopBarTextButton(
            buttonText = "텍스트버튼",
            onClick = {},
            enabled = false,
        )
    }
}
