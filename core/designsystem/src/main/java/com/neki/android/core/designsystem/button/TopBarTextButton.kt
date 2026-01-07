package com.neki.android.core.designsystem.button

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun TopBarTextButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Text(
        text = buttonText,
        modifier = modifier
            .clickableSingle(onClick = onClick)
            .padding(vertical = 10.dp),
        style = NekiTheme.typography.body16SemiBold,
        color = NekiTheme.colorScheme.primary500,
    )
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
