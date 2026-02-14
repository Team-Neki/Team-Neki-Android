package com.neki.android.feature.archive.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.main.component.EmptyContent

@Composable
internal fun EmptyAlbumContent(
    title: String,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        EmptyTopBar(
            title = title,
            onClickBack = onClickBack,
        )

        EmptyContent(
            modifier = Modifier.align(Alignment.Center),
            emptyText = "아직 등록된 사진이 없어요\n찍은 네컷을 저장해보세요!",
        )
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
private fun EmptyAlbumContentPreview() {
    NekiTheme {
        EmptyAlbumContent(
            title = "즐겨찾기",
        )
    }
}
