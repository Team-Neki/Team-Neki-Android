package com.neki.android.feature.archive.impl.photo.component

import androidx.compose.foundation.background
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
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.photo.AllPhotoIntent
import com.neki.android.feature.archive.impl.photo.AllPhotoState

@Composable
internal fun AllPhotoEmptyContent(
    uiState: AllPhotoState,
    modifier: Modifier = Modifier,
    onIntent: (AllPhotoIntent) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        AllPhotoTopBar(
            selectMode = uiState.selectMode,
            onClickBack = { onIntent(AllPhotoIntent.ClickTopBarBackIcon) },
            onClickSelect = { },
            onClickCancel = { },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_empty_content),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
                Text(
                    text = "아직 등록된 사진이 없어요\n새로운 사진을 등록하고 앨범에 추가해보세요!",
                    style = NekiTheme.typography.body14Medium,
                    color = NekiTheme.colorScheme.gray300,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun AllPhotoEmptyContentPreview() {
    NekiTheme {
        AllPhotoEmptyContent(
            uiState = AllPhotoState(),
        )
    }
}
