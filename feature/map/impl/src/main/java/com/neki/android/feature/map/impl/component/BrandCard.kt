package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.HorizontalSpacer
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.const.FourCutBrand

@Composable
fun BrandCard(
    brand: FourCutBrand,
    modifier: Modifier = Modifier,
    onClickDirection: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .buttonShadow(
                shape = RoundedCornerShape(20.dp),
                blurRadius = 4.dp
            )
            .background(
                shape = RoundedCornerShape(20.dp),
                color = NekiTheme.colorScheme.white
            )
            .padding(16.dp),
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
        HorizontalSpacer(1f)
        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = NekiTheme.colorScheme.gray900
                )
                .noRippleClickableSingle(onClick = onClickDirection)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier.size(24.dp),
                model = R.drawable.icon_find_direction,
                contentDescription = null,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun BrandCardPreview() {
    NekiTheme {
        BrandCard(brand = FourCutBrand.LIFE_FOUR_CUT)
    }
}
