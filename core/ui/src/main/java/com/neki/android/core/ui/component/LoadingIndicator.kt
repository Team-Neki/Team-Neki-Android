package com.neki.android.core.ui.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    circleColor: Color = NekiTheme.colorScheme.primary300,
    backgroundColor: Color = NekiTheme.colorScheme.primary100,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        CircularProgressIndicator(
            modifier = modifier,
            strokeWidth = 6.dp,
            color = circleColor,
            trackColor = backgroundColor,
        )
    }
}

@ComponentPreview
@Composable
private fun LoadingIndicatorPreview() {
    NekiTheme {
        LoadingIndicator(
            onDismissRequest = {},
        )
    }
}
