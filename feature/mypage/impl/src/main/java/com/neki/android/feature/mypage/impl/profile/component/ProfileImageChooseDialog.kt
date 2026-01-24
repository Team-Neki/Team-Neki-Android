package com.neki.android.feature.mypage.impl.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun ProfileImageChooseDialog(
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onDismissRequest: () -> Unit = {},
    onClickDefaultProfile: () -> Unit = {},
    onClickSelectPhoto: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .widthIn(max = 400.dp)
                .background(
                    shape = RoundedCornerShape(20.dp),
                    color = NekiTheme.colorScheme.white,
                )
                .padding(vertical = 12.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableSingle(onClick = onClickDefaultProfile)
                    .padding(vertical = 14.dp),
                text = "기본 프로필로 바꾸기",
                style = NekiTheme.typography.body16SemiBold,
                color = NekiTheme.colorScheme.gray800,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableSingle(onClick = onClickSelectPhoto)
                    .padding(vertical = 14.dp),
                text = "사진 선택하기",
                style = NekiTheme.typography.body16SemiBold,
                color = NekiTheme.colorScheme.gray800,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ProfileImageChooseDialogPreview() {
    NekiTheme {
        ProfileImageChooseDialog()
    }
}
