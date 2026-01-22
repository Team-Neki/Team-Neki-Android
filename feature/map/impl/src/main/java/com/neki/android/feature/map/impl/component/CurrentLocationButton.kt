package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun CurrentLocationButton(
    isActiveCurrentLocation: Boolean,
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
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(
                if (isActiveCurrentLocation) R.drawable.icon_current_location_on else R.drawable.icon_current_location_off,
            ),
            contentDescription = null,
            tint = Color.Unspecified,
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
