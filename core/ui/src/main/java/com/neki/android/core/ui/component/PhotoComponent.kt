package com.neki.android.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo

@Composable
fun PhotoComponent(
    photo: Photo,
    modifier: Modifier = Modifier,
    onItemClick: (Photo) -> Unit = {},
    additionalContent: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .noRippleClickable { onItemClick(photo) },
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            model = photo.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )

        additionalContent()
    }
}

@ComponentPreview
@Composable
private fun PhotoComponentPreview() {
    NekiTheme {
        PhotoComponent(
            modifier = Modifier.size(120.dp),
            photo = Photo(),
        )
    }
}
