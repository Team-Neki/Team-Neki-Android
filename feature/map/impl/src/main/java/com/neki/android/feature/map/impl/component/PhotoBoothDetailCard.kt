package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PhotoBooth
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
internal fun PhotoBoothDetailCard(
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
        PhotoBoothCard(
            photoBooth = photoBooth,
            onClick = onClickCard,
            onClickDirection = onClickDirection,
        )
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
