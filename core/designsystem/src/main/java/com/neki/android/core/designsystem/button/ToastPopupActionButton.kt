package com.neki.android.core.designsystem.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun ToastPopupActionButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickableSingle(onClick = onClick)
            .background(
                color = NekiTheme.colorScheme.gray700,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = buttonText,
            style = NekiTheme.typography.body14Medium,
            color = NekiTheme.colorScheme.gray25,
        )
    }
}

@ComponentPreview
@Composable
private fun ToastPopupActionButtonPreview() {
    NekiTheme {
        ToastPopupActionButton(
            buttonText = "텍스트",
            onClick = {},
        )
    }
}
