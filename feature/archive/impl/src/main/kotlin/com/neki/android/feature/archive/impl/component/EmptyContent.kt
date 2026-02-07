package com.neki.android.feature.archive.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

private const val EMPTY_TEXT = "아직 등록된 사진이 없어요\n새로운 사진을 등록하고 앨범에 추가해보세요!"

@Composable
internal fun EmptyContent(
    title: String,
    modifier: Modifier = Modifier,
    emptyText: String = EMPTY_TEXT,
    onClickBack: () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        EmptyTopBar(
            title = title,
            onClickBack = onClickBack,
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.icon_empty_content),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Text(
                text = emptyText,
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.gray300,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun EmptyTopBar(
    title: String,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackTitleTopBar(
        modifier = modifier,
        title = title,
        onBack = onClickBack,
    )
}

@ComponentPreview
@Composable
private fun EmptyContentPreview() {
    NekiTheme {
        EmptyContent(
            title = "즐겨찾기",
        )
    }
}
