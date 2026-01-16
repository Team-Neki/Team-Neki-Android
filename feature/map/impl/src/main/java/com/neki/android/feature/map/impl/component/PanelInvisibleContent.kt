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
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.map.impl.const.FourCutBrand

@Composable
fun PanelInvisibleContent(
    modifier: Modifier = Modifier,
    onClickCurrentLocation: () -> Unit = {},
    onClickCloseCard: () -> Unit = {},
    onClickDirection: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CurrentLocationButton(
                isActiveCurrentLocation = false,
                onClick = onClickCurrentLocation
            )
            BrandCardCloseButton(onClick = onClickCloseCard)
        }
        VerticalSpacer(12.dp)
        BrandCard(
            brand = FourCutBrand.LIFE_FOUR_CUT,
            onClickDirection = {}
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
                color = NekiTheme.colorScheme.white
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onClick),
            model = R.drawable.icon_close_20,
            contentDescription = null
        )
    }
}

@ComponentPreview
@Composable
private fun PanelInvisibleContentPreview() {
    NekiTheme {
        PanelInvisibleContent()
    }
}
