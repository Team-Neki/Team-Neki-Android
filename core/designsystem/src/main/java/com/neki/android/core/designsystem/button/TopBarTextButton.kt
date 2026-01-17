package com.neki.android.core.designsystem.button

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun TopBarTextButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
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
            color = NekiTheme.colorScheme.primary500,
        )
    }
}

@ComponentPreview
@Composable
private fun TopBarTextButtonPreview() {
    NekiTheme {
        TopBarTextButton(
            buttonText = "텍스트버튼",
            onClick = {},
        )
    }
}
