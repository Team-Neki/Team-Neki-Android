package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.util.formatDistance

@Composable
internal fun HorizontalBrandItem(
    photoBooth: PhotoBooth,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onClickItem: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickableSingle(onClick = onClickItem),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(64.dp),
            model = photoBooth.imageUrl,
            placeholder = painterResource(R.drawable.icon_photo_booth_empty),
            error = painterResource(R.drawable.icon_photo_booth_empty),
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = photoBooth.brandName,
                style = NekiTheme.typography.title20SemiBold,
                color = NekiTheme.colorScheme.gray900,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = photoBooth.branchName,
                    style = NekiTheme.typography.body14Medium,
                    color = NekiTheme.colorScheme.gray600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 10.dp)
                        .background(color = NekiTheme.colorScheme.gray100),
                )
                Text(
                    text = photoBooth.distance.formatDistance(),
                    color = NekiTheme.colorScheme.gray700,
                    style = NekiTheme.typography.body14SemiBold,
                )
            }
        }
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(
                if (isFavorite) R.drawable.icon_heart_filled else R.drawable.icon_heart_stroked,
            ),
            contentDescription = null,
            tint = if (isFavorite) NekiTheme.colorScheme.primary400 else NekiTheme.colorScheme.gray100,
        )
    }
}

@ComponentPreview
@Composable
private fun HorizontalBrandItemPreview() {
    NekiTheme {
        HorizontalBrandItem(
            photoBooth = PhotoBooth(
                brandName = "인생네컷",
                branchName = "사당역점",
                distance = 320,
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun HorizontalBrandItemFavoritePreview() {
    NekiTheme {
        HorizontalBrandItem(
            photoBooth = PhotoBooth(
                brandName = "인생네컷",
                branchName = "사당역점",
                distance = 320,
            ),
            isFavorite = true,
        )
    }
}

@ComponentPreview
@Composable
private fun HorizontalBrandItemLongTextPreview() {
    NekiTheme {
        HorizontalBrandItem(
            photoBooth = PhotoBooth(
                brandName = "인생네컷",
                branchName = "지점명이매우길어지는경우테스트용지점네임",
                distance = 1200,
            ),
        )
    }
}
