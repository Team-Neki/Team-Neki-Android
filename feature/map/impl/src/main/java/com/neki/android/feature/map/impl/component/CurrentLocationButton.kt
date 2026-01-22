package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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

@Composable
internal fun CurrentLocationButton(
    modifier: Modifier = Modifier,
    isActiveCurrentLocation: Boolean,
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
            model = if (isActiveCurrentLocation) R.drawable.icon_current_location_on else R.drawable.icon_current_location_off,
            contentDescription = null,
        )
    }
}

@ComponentPreview
@Composable
private fun CurrentLocationButtonOffPreview() {
    NekiTheme {
        CurrentLocationButton(isActiveCurrentLocation = false)
    }
}

@ComponentPreview
@Composable
private fun CurrentLocationButtonOnPreview() {
    NekiTheme {
        CurrentLocationButton(isActiveCurrentLocation = true)
    }
}
