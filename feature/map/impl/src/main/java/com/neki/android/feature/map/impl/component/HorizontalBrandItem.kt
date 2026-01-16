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
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.HorizontalSpacer
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.const.FourCutBrand

@Composable
fun HorizontalBrandItem(
    brand: FourCutBrand,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickableSingle(onClick = onItemClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(64.dp),
            model = brand.logoRes,
            contentDescription = null,
        )
        HorizontalSpacer(16.dp)
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = brand.brandName,
                    color = NekiTheme.colorScheme.gray900,
                    style = NekiTheme.typography.title18Bold
                )
                HorizontalSpacer(4.dp)
                Text(
                    text = "사당역점",
                    color = NekiTheme.colorScheme.gray600,
                    style = NekiTheme.typography.caption12Medium
                )
            }
            VerticalSpacer(4.dp)
            Text(
                text = "300m",
                color = NekiTheme.colorScheme.gray400,
                style = NekiTheme.typography.caption12Medium
            )
        }
    }
}

@ComponentPreview
@Composable
private fun HorizontalBrandItemPreview() {
    NekiTheme {
        HorizontalBrandItem(brand = FourCutBrand.LIFE_FOUR_CUT)
    }
}
