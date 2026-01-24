package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.photoBackground
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.PhotoComponent

@Composable
internal fun ArchiveMainPhotoItem(
    photo: Photo,
    modifier: Modifier = Modifier,
    onClickItem: (Photo) -> Unit = {},
) {
    PhotoComponent(
        photo = photo,
        modifier = modifier.photoBackground(),
        onClickItem = onClickItem,
    ) {
        if (photo.isFavorite) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_heart),
                contentDescription = null,
                tint = NekiTheme.colorScheme.white,
            )
        }
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
