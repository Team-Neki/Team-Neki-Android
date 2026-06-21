package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.cardShadow
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.ui.compose.HorizontalSpacer
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.util.formatDistance

@Composable
internal fun PhotoBoothDetailContent(
    photoBooth: PhotoBooth,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onClickFavorite: () -> Unit = {},
    onClickCloseCard: () -> Unit = {},
    onClickCard: () -> Unit = {},
    onClickDirection: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PhotoBoothFavoriteButton(
                isFavorite = isFavorite,
                onClick = onClickFavorite,
            )
            CloseButton(onClick = onClickCloseCard)
        }
        PhotoBoothDetailCard(
            photoBooth = photoBooth,
            onClick = onClickCard,
            onClickDirection = onClickDirection,
        )
    }
}

@Composable
private fun PhotoBoothDetailCard(
    photoBooth: PhotoBooth,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onClickDirection: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(shape = RoundedCornerShape(20.dp))
            .background(
                shape = RoundedCornerShape(20.dp),
                color = NekiTheme.colorScheme.white,
            )
            .noRippleClickableSingle(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
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
        HorizontalSpacer(12.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = photoBooth.brandName,
                color = NekiTheme.colorScheme.gray900,
                style = NekiTheme.typography.title20SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = photoBooth.branchName,
                    color = NekiTheme.colorScheme.gray600,
                    style = NekiTheme.typography.body14Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 10.dp)
                        .background(color = NekiTheme.colorScheme.gray100)
                )
                Text(
                    text = photoBooth.distance.formatDistance(),
                    color = NekiTheme.colorScheme.gray700,
                    style = NekiTheme.typography.body14SemiBold,
                )
            }
        }
        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = NekiTheme.colorScheme.gray900,
                )
                .noRippleClickableSingle(onClick = onClickDirection)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.icon_road),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothDetailCardPreview() {
    NekiTheme {
        PhotoBoothDetailCard(
            photoBooth = PhotoBooth(
                brandName = "인생네컷",
                branchName = "사당역점",
                distance = 300,
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothDetailCardLongTextPreview() {
    NekiTheme {
        PhotoBoothDetailCard(
            photoBooth = PhotoBooth(
                brandName = "브랜드명이매우길어지는경우테스트용브랜드네임",
                branchName = "지점명이매우길어지는경우테스트용지점네임",
                distance = 1200,
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothDetailContentPreview() {
    NekiTheme {
        PhotoBoothDetailContent(
            photoBooth = PhotoBooth(
                brandName = "인생네컷",
                branchName = "사당역점",
                distance = 300,
            ),
        )
    }
}
