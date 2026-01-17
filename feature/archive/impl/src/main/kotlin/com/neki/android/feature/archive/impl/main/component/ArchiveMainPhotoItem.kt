package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo

@Composable
internal fun ArchiveMainPhotoItem(
    photo: Photo,
    modifier: Modifier = Modifier,
    onItemClick: (Photo) -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .noRippleClickable { onItemClick(photo) },
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = photo.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color.Black.copy(alpha = 0f),
                            0.7f to Color.Black.copy(alpha = 0.3f),
                            1f to Color.Black.copy(alpha = 1f),
                        ),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f),
                    ),
                ),
        )
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_heart_white_20),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@ComponentPreview
@Composable
private fun ArchiveMainPhotoGridContentPreview() {
    NekiTheme {
        ArchiveMainPhotoItem(
            photo = Photo(),
        )
    }
}
