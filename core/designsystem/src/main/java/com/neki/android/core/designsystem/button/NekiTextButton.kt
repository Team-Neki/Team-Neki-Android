package com.neki.android.core.designsystem.button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.extension.MultipleEventsCutter
import com.neki.android.core.designsystem.extension.get
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun NekiTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    multipleEventsCutterEnabled: Boolean = true,
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
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
                text = "Text Button",
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}
