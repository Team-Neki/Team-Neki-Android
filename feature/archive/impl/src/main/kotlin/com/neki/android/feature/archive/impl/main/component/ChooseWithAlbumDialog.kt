package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun ChooseWithAlbumDialog(
    onDismissRequest: () -> Unit = {},
    onUploadWithOutAlbumClick: () -> Unit = {},
    onUploadWithAlbumClick: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 400.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(NekiTheme.colorScheme.white)
                .padding(vertical = 12.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableSingle(onClick = onUploadWithOutAlbumClick)
                    .padding(vertical = 14.dp),
                text = "앨범 없이 업로드하기",
                style = NekiTheme.typography.body16SemiBold,
                color = NekiTheme.colorScheme.gray800,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableSingle(onClick = onUploadWithAlbumClick)
                    .padding(vertical = 14.dp),
                text = "앨범 선택 후 업로드하기",
                style = NekiTheme.typography.body16SemiBold,
                color = NekiTheme.colorScheme.gray800,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ChooseWithAlbumDialogPreview() {
    NekiTheme {
        ChooseWithAlbumDialog()
    }
}
