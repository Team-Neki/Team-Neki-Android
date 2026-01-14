package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.const.FourCutBrand

@Composable
fun VerticalFourCutBrand(
    brand: FourCutBrand,
    modifier: Modifier = Modifier,
    onItemClick: (Boolean) -> Unit = {}
) {
    var isSelected by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .width(66.dp)
            .noRippleClickable {
                isSelected = !isSelected
                onItemClick(isSelected)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(56.dp)
        ) {
            AsyncImage(
                modifier = Modifier.clip(CircleShape),
                model = brand.logoRes,
                contentDescription = null,
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .background(
                            shape = CircleShape,
                            color = Color(0xFFFF5647).copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier.size(28.dp),
                        model = R.drawable.icon_check_white,
                        contentDescription = null,
                    )
                }
            }
        }
        VerticalSpacer(8.dp)
        Text(
            text = brand.brandName,
            color = if (isSelected) NekiTheme.colorScheme.primary400 else NekiTheme.colorScheme.gray900,
            style = NekiTheme.typography.body14Medium,
            textAlign = TextAlign.Center
        )
    }
}

@ComponentPreview
@Composable
private fun VerticalFourCutBrandPreview() {
    NekiTheme {
        VerticalFourCutBrand(brand = FourCutBrand.LIFE_FOUR_CUT)
    }
}
