package com.neki.android.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    onClickItem: (Photo) -> Unit = {},
) {
    val hasSize = photo.width != null && photo.height != null
    var aspectRatio by if (hasSize) {
        remember { mutableFloatStateOf(photo.width!! / photo.height!!.toFloat()) }
    } else {
        rememberSaveable { mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .noRippleClickable { onClickItem(photo) },
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (aspectRatio > 0f) Modifier.aspectRatio(aspectRatio)
                    else Modifier,
                ),
            model = photo.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            onSuccess = { state ->
                if (!hasSize) {
                    val size = state.painter.intrinsicSize
                    if (size.width > 0f && size.height > 0f) {
                        aspectRatio = size.width / size.height
                    }
                }
            },
        )
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
