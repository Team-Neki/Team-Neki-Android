package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun EmptyContent(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(
                        color = NekiTheme.colorScheme.gray50,
                        shape = CircleShape,
                    ),
            )
            Text(
                text = if (isFavorite) "아직 등록된 사진이 없어요\n아카이빙 페이지에서 추가해보세요!"
                else "아직 등록된 사진이 없어요\n아카이빙 페이지에서 추가해보세요!",
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.gray300,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun FavoriteEmptyContentPreview() {
    NekiTheme {
        EmptyContent(
            isFavorite = true,
        )
    }
}

@ComponentPreview
@Composable
private fun EmptyContentPreview() {
    NekiTheme {
        EmptyContent(
            isFavorite = false,
        )
    }
}
