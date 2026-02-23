package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.PhotoComponent
import com.neki.android.core.ui.component.GridItemOverlay

@Composable
internal fun ArchiveMainPhotoItem(
    photo: Photo,
    modifier: Modifier = Modifier,
    onClickItem: (Photo) -> Unit = {},
    onClickFavorite: (Photo) -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        PhotoComponent(
            photo = photo,
            onClickItem = onClickItem,
        )

        GridItemOverlay(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(8.dp),
        )

        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .noRippleClickableSingle { onClickFavorite(photo) }
                .padding(10.dp)
                .size(20.dp),
            imageVector = ImageVector.vectorResource(
                if (photo.isFavorite) R.drawable.icon_favorite_filled
                else R.drawable.icon_favorite_stroked_alpha_filled,
            ),
            contentDescription = null,
            tint = NekiTheme.colorScheme.white,
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
