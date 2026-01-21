package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.BrandInfo
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
fun PanelInvisibleContent(
    brandInfo: BrandInfo,
    modifier: Modifier = Modifier,
    isCurrentLocation: Boolean = false,
    onClickCurrentLocation: () -> Unit = {},
    onClickCloseCard: () -> Unit = {},
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
            BrandCardCloseButton(onClick = onClickCloseCard)
        }
        VerticalSpacer(12.dp)
        BrandCard(
            brand = brandInfo,
            onClickDirection = onClickDirection,
        )
    }
}

@Composable
fun BrandCardCloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .buttonShadow(blurRadius = 10.dp)
            .background(
                shape = CircleShape,
                color = NekiTheme.colorScheme.white,
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier.size(20.dp),
            model = R.drawable.icon_close_20,
            contentDescription = null,
        )
    }
}

@ComponentPreview
@Composable
private fun PanelInvisibleContentPreview() {
    NekiTheme {
        PanelInvisibleContent(
            brandInfo = BrandInfo(
                brandName = "인생네컷",
                brandImageRes = R.drawable.icon_life_four_cut,
                branchName = "사당역점",
                distance = "300m",
            ),
        )
    }
}
