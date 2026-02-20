package com.neki.android.feature.archive.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun EmptyPhotoContent(
    emptyText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(
            modifier = Modifier
                .width(148.dp)
                .height(112.dp),
            painter = painterResource(R.drawable.image_empty_photo),
            contentDescription = null,
        )
        Text(
            text = emptyText,
            style = NekiTheme.typography.body14Medium,
            color = NekiTheme.colorScheme.gray200,
            textAlign = TextAlign.Center,
        )
    }
}

@ComponentPreview
@Composable
private fun EmptyPhotoContentPreview() {
    NekiTheme {
        EmptyPhotoContent(
            emptyText = "아직 등록된 사진이 없어요\n찍은 네컷을 네키에 저장해보세요!",
        )
    }
}
