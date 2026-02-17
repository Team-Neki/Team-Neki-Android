package com.neki.android.feature.archive.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun EmptyAlbumContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        EmptyPhotoContent(
            modifier = Modifier.align(Alignment.Center),
            emptyText = "아직 등록된 사진이 없어요\n새로운 사진을 등록하고 앨범에 추가해보세요!",
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
