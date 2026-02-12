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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    isCurrentLocation: Boolean = false,
    onClickCurrentLocation: () -> Unit = {},
    onClickCloseCard: () -> Unit = {},
    onClickCard: () -> Unit = {},
    onClickDirection: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CurrentLocationButton(
                isActiveCurrentLocation = isCurrentLocation,
                onClick = onClickCurrentLocation,
            )
            CloseButton(onClick = onClickCloseCard)
        }
        VerticalSpacer(12.dp)
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
            placeholder = painterResource(R.drawable.icon_life_four_cut),
            error = painterResource(R.drawable.icon_life_four_cut),
            contentDescription = null,
        )
        HorizontalSpacer(16.dp)
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = photoBooth.brandName,
                    color = NekiTheme.colorScheme.gray900,
                    style = NekiTheme.typography.title18Bold,
                )
                HorizontalSpacer(4.dp)
                Text(
                    text = photoBooth.branchName,
                    color = NekiTheme.colorScheme.gray600,
                    style = NekiTheme.typography.caption12Medium,
                )
            }
            VerticalSpacer(4.dp)
            Text(
                text = photoBooth.distance.formatDistance(),
                color = NekiTheme.colorScheme.gray400,
                style = NekiTheme.typography.caption12Medium,
            )
        }
        HorizontalSpacer(1f)
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
