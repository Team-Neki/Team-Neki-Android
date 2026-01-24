package com.neki.android.feature.archive.impl.photo_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun PhotoDetailActionBar(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .noRippleClickable { onDownloadClick() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_download),
                contentDescription = null,
                tint = NekiTheme.colorScheme.gray500,
            )

            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .noRippleClickable { onFavoriteClick() },
                imageVector = if (isFavorite) {
                    ImageVector.vectorResource(R.drawable.icon_heart_primary_28)
                } else {
                    ImageVector.vectorResource(R.drawable.icon_heart_gray_stroke_28)
                },
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }

        Icon(
            modifier = Modifier
                .size(28.dp)
                .noRippleClickable { onDeleteClick() },
            imageVector = ImageVector.vectorResource(R.drawable.icon_trash),
            contentDescription = null,
            tint = NekiTheme.colorScheme.gray600,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarNotFavoritePreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = false,
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoDetailActionBarFavoritePreview() {
    NekiTheme {
        PhotoDetailActionBar(
            isFavorite = true,
        )
    }
}
