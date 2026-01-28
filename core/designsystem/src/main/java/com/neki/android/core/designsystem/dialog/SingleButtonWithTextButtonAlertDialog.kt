package com.neki.android.core.designsystem.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun SingleButtonWithTextButtonAlertDialog(
    title: String,
    content: String,
    buttonText: String,
    textButtonText: String,
    onDismissRequest: () -> Unit,
    onButtonClick: () -> Unit,
    onTextButtonClick: () -> Unit,
    enabled: Boolean = true,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
    ),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 400.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(NekiTheme.colorScheme.white)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.icon_dialog_alert),
                tint = Color.Unspecified,
                contentDescription = null,
            )
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = NekiTheme.typography.title18Bold,
                    color = NekiTheme.colorScheme.gray900,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = content,
                    style = NekiTheme.typography.body14Regular,
                    color = NekiTheme.colorScheme.gray500,
                    textAlign = TextAlign.Center,
                )
            }
            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CTAButtonPrimary(
                    text = buttonText,
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    enabled = enabled,
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickableSingle(onClick = onTextButtonClick),
                    text = textButtonText,
                    style = NekiTheme.typography.body14Regular,
                    color = NekiTheme.colorScheme.primary600,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun SingleButtonWithTextButtonAlertDialogPreview() {
    NekiTheme {
        SingleButtonWithTextButtonAlertDialog(
            title = "메인 텍스트가 들어가는 곳",
            content = "보조 설명 텍스트가 들어가는 공간입니다",
            buttonText = "텍스트",
            textButtonText = "텍스트 공간",
            onDismissRequest = {},
            onButtonClick = {},
            onTextButtonClick = {},
        )
    }
}
