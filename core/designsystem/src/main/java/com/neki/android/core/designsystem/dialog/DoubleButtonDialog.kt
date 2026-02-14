package com.neki.android.core.designsystem.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.CTAButtonGray
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun DoubleButtonDialog(
    title: String,
    content: String,
    grayButtonText: String,
    primaryButtonText: String,
    onDismissRequest: () -> Unit,
    onClickPrimaryButton: () -> Unit,
    onClickGrayButton: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
    ),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 400.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(NekiTheme.colorScheme.white)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CTAButtonGray(
                    text = grayButtonText,
                    onClick = onClickGrayButton,
                    modifier = Modifier.weight(1f),
                )
                CTAButtonPrimary(
                    text = primaryButtonText,
                    onClick = onClickPrimaryButton,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun DoubleButtonDialogPreview() {
    NekiTheme {
        DoubleButtonDialog(
            title = "메인 텍스트가 들어가는 곳",
            content = "보조 설명 텍스트가 들어가는 공간입니다",
            grayButtonText = "텍스트",
            primaryButtonText = "텍스트",
            onDismissRequest = {},
            onClickPrimaryButton = {},
            onClickGrayButton = {},
        )
    }
}
