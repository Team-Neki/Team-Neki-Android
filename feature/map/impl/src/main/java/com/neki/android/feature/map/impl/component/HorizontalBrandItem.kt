package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.layout.Column
import com.neki.android.feature.map.impl.util.formatDistance
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.ui.compose.HorizontalSpacer
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
internal fun HorizontalBrandItem(
    photoBooth: PhotoBooth,
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickableSingle(onClick = onClickItem),
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
