package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Brand
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
internal fun VerticalBrandItem(
    brand: Brand,
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .widthIn(max = 355.dp)
            .noRippleClickable { onClickItem() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                model = brand.imageUrl,
                placeholder = painterResource(R.drawable.icon_life_four_cut),
                error = painterResource(R.drawable.icon_life_four_cut),
                contentDescription = null,
            )

            if (brand.isChecked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            shape = CircleShape,
                            color = Color(0xFFFF5647).copy(alpha = 0.5f),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_check),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.white,
                    )
                }
            }
        }
        VerticalSpacer(8.dp)
        Text(
            text = brand.name,
            color = if (brand.isChecked) NekiTheme.colorScheme.primary400 else NekiTheme.colorScheme.gray900,
            style = NekiTheme.typography.body14Medium,
            textAlign = TextAlign.Center,
        )
    }
}

@ComponentPreview
@Composable
private fun CheckedVerticalBrandItemPreview() {
    NekiTheme {
        VerticalBrandItem(
            brand = Brand(
                isChecked = true,
                name = "인생네컷",
            ),
        )
    }
}

@ComponentPreview
@Composable
private fun NotCheckedVerticalBrandItemPreview() {
    NekiTheme {
        VerticalBrandItem(
            brand = Brand(
                isChecked = false,
                name = "인생네컷",
                imageUrl = "https://dev-yapp.suitestudy.com:4641/file/image/logo/LIFEFOURCUTS_LOGO_v1.png"
            ),
        )
    }
}
