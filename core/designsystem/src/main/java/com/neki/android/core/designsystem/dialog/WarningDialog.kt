package com.neki.android.core.designsystem.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun WarningDialog(
    content: String,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(NekiTheme.colorScheme.white),
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(24.dp)
                    .clickableSingle(onClick = onDismissRequest),
                imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                tint = NekiTheme.colorScheme.gray900,
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 20.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_dialog_alert),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )

                Text(
                    text = content,
                    style = NekiTheme.typography.body14Regular,
                    color = NekiTheme.colorScheme.gray500,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun WarningDialogPreview() {
    NekiTheme {
        WarningDialog(
            content = "텍스트가 들어가는 자리입니다.\n2줄 이상이면 이렇게 돼요.",
            onDismissRequest = {},
        )
    }
}
