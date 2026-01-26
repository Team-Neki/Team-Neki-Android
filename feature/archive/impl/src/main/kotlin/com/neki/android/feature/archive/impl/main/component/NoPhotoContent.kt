package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun NoPhotoContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(104.dp)
                .background(
                    color = NekiTheme.colorScheme.gray50,
                    shape = CircleShape,
                ),
        )
        Text(
            text = "아직 등록된 사진이 없어요\n찍은 네컷을 네키에 저장해보세요!",
            style = NekiTheme.typography.body14Medium,
            color = NekiTheme.colorScheme.gray300,
            textAlign = TextAlign.Center,
        )
    }
}
