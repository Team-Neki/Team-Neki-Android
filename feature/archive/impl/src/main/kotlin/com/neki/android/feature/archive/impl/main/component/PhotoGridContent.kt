package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.extension.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo

@Composable
internal fun PhotoItem(
    photo: Photo,
    modifier: Modifier = Modifier,
    onItemClick: (Photo) -> Unit = {},
) {
    AsyncImage(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .noRippleClickable { onItemClick(photo) },
        model = photo.imageUrl,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}

@ComponentPreview
@Composable
private fun PhotoGridContentPreview() {
    NekiTheme {
        PhotoItem(
            photo = Photo(),
        )
    }
}
