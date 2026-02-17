package com.neki.android.feature.archive.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.main.component.EmptyContent

@Composable
internal fun EmptyAlbumContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        EmptyContent(
            modifier = Modifier.align(Alignment.Center),
            emptyText = "아직 등록된 사진이 없어요\n찍은 네컷을 저장해보세요!",
        )
    }
}

@ComponentPreview
@Composable
private fun EmptyAlbumContentPreview() {
    NekiTheme {
        EmptyAlbumContent()
    }
}
