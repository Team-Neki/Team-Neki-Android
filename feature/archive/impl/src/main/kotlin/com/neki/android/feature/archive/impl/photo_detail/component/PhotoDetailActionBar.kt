package com.neki.android.feature.archive.impl.photo_detail.component

import android.R.attr.contentDescription
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.actionbar.NekiBothSidesActionBar
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun PhotoDetailActionBar(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onClickDownload: () -> Unit = {},
    onClickFavorite: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    NekiBothSidesActionBar(
        modifier = modifier.fillMaxWidth(),
        startContent = {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .noRippleClickable { onClickDownload() },
                    imageVector = ImageVector.vectorResource(R.drawable.icon_download),
                    contentDescription = null,
                    tint = NekiTheme.colorScheme.gray700,
                )

                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .noRippleClickable { onClickFavorite() },
                    imageVector = ImageVector.vectorResource(
                        if (isFavorite) R.drawable.icon_heart_filled
                        else R.drawable.icon_heart_stroked,
                    ),
                    contentDescription = null,
                    tint = if (isFavorite) NekiTheme.colorScheme.primary400
                    else NekiTheme.colorScheme.gray700,
                )
            }
        },
        endContent = {
            Icon(
                modifier = Modifier
                    .padding(20.dp)
                    .size(28.dp)
                    .noRippleClickable { onClickDelete() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_trash),
                contentDescription = null,
                tint = NekiTheme.colorScheme.gray700,
            )
        },
    )
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
