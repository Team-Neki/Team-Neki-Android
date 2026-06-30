package com.neki.android.feature.map.impl.component

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.buttonShadow
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
private fun MapCircleButton(
    @DrawableRes iconRes: Int,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .buttonShadow()
            .clip(CircleShape)
            .background(
                shape = CircleShape,
                color = NekiTheme.colorScheme.white,
            )
            .clickableSingle(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            tint = tint,
        )
    }
}

@Composable
internal fun CurrentLocationButton(
    isActiveCurrentLocation: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MapCircleButton(
        iconRes = R.drawable.icon_current_position,
        tint = if (isActiveCurrentLocation) NekiTheme.colorScheme.primary500
        else NekiTheme.colorScheme.gray800,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
internal fun PhotoBoothFavoriteButton(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MapCircleButton(
        iconRes = if (isFavorite) R.drawable.icon_heart_gradient
        else R.drawable.icon_heart_stroked,
        tint = Color.Unspecified,
        modifier = modifier,
        onClick = onClick,
    )
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

@ComponentPreview
@Composable
private fun PhotoBoothFavoriteButtonOffPreview() {
    NekiTheme {
        PhotoBoothFavoriteButton(isFavorite = false)
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothFavoriteButtonOnPreview() {
    NekiTheme {
        PhotoBoothFavoriteButton(isFavorite = true)
    }
}
