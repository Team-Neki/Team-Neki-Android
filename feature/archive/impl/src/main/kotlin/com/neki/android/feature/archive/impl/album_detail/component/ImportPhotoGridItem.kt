package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.component.SelectionCheckbox

@Composable
internal fun ImportPhotoGridItem(
    photo: Photo,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, NekiTheme.colorScheme.primary400)
                } else {
                    Modifier
                },
            ),
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { onToggleSelect() },
            model = photo.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        SelectionCheckbox(
            isSelected = isSelected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .noRippleClickable { onToggleSelect() },
            unselectedColor = NekiTheme.colorScheme.white.copy(alpha = 0.2f),
        )
    }
}
