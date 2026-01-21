package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.layout.Column
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
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.BrandInfo
import com.neki.android.core.ui.compose.HorizontalSpacer
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
fun HorizontalBrandItem(
    brandInfo: BrandInfo,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickableSingle(onClick = onItemClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(64.dp),
            model = brandInfo.brandImageRes,
            contentDescription = null,
        )
        HorizontalSpacer(16.dp)
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = brandInfo.brandName,
                    color = NekiTheme.colorScheme.gray900,
                    style = NekiTheme.typography.title18Bold,
                )
                HorizontalSpacer(4.dp)
                Text(
                    text = brandInfo.branchName,
                    color = NekiTheme.colorScheme.gray600,
                    style = NekiTheme.typography.caption12Medium,
                )
            }
            VerticalSpacer(4.dp)
            Text(
                text = brandInfo.distance,
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
            brandInfo = BrandInfo(
                brandName = "인생네컷",
                brandImageRes = R.drawable.icon_life_four_cut,
                branchName = "사당역점",
                distance = "320m",
            ),
        )
    }
}
