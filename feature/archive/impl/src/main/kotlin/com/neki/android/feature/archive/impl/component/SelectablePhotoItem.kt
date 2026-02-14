package com.neki.android.feature.archive.impl.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.PhotoComponent
import com.neki.android.core.ui.component.PhotoGridItemOverlay
import com.neki.android.core.ui.component.SelectedPhotoGridItemOverlay
import com.neki.android.core.ui.component.SelectionCheckbox

@Composable
internal fun SelectablePhotoItem(
    photo: Photo,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isSelectMode: Boolean = false,
    onClickItem: (Photo) -> Unit = {},
    onClickSelect: (Photo) -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        PhotoComponent(
            photo = photo,
            modifier = Modifier.then(
                if (isSelected)
                    Modifier.border(
                        width = 2.dp,
                        color = NekiTheme.colorScheme.primary400,
                        shape = RoundedCornerShape(8.dp),
                    ) else Modifier.clip(RoundedCornerShape(8.dp)),
            ),
            onClickItem = onClickItem,
        )

        if (isSelected) {
            SelectedPhotoGridItemOverlay(
                modifier = Modifier.matchParentSize(),
                shape = RoundedCornerShape(8.dp),
            )
        } else {
            PhotoGridItemOverlay(
                modifier = Modifier.matchParentSize(),
                shape = RoundedCornerShape(8.dp),
            )
        }

        if (photo.isFavorite) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_heart_filled),
                contentDescription = null,
                tint = NekiTheme.colorScheme.white,
            )
        }

        if (isSelectMode) {
            SelectionCheckbox(
                isSelected = isSelected,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 12.dp, start = 12.dp)
                    .noRippleClickable { onClickSelect(photo) },
                unselectedColor = NekiTheme.colorScheme.white.copy(alpha = 0.2f),
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SelectablePhotoItemUnselectedPreview() {
    NekiTheme {
        SelectablePhotoItem(
            photo = Photo(
                id = 1,
                imageUrl = "https://picsum.photos/200/300",
                isFavorite = false,
            ),
            isSelected = false,
        )
    }
}

@ComponentPreview
@Composable
private fun SelectablePhotoItemSelectedPreview() {
    NekiTheme {
        SelectablePhotoItem(
            isSelectMode = true,
            photo = Photo(
                id = 1,
                imageUrl = "https://picsum.photos/200/300",
                isFavorite = false,
            ),
            isSelected = true,
        )
    }
}

@ComponentPreview
@Composable
private fun SelectablePhotoItemFavoriteUnselectedPreview() {
    NekiTheme {
        SelectablePhotoItem(
            isSelectMode = true,
            photo = Photo(
                id = 1,
                imageUrl = "https://picsum.photos/200/300",
                isFavorite = true,
            ),
            isSelected = false,
        )
    }
}

@ComponentPreview
@Composable
private fun SelectablePhotoItemFavoriteSelectedPreview() {
    NekiTheme {
        SelectablePhotoItem(
            isSelectMode = true,
            photo = Photo(
                id = 1,
                imageUrl = "https://picsum.photos/200/300",
                isFavorite = true,
            ),
            isSelected = true,
        )
    }
}
