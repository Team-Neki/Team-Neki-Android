package com.neki.android.feature.map.impl.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.Dp
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
    iconSize: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
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
        iconSize = 28.dp,
        modifier = modifier
            .buttonShadow()
            .clip(CircleShape)
            .background(shape = CircleShape, color = NekiTheme.colorScheme.white)
            .clickableSingle(onClick = onClick)
            .padding(6.dp),
    )
}

@Composable
internal fun ShowFavoritePhotoBoothButton(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MapCircleButton(
        iconRes = if (isFavorite) R.drawable.icon_heart_gradient
        else R.drawable.icon_heart_stroked,
        tint = Color.Unspecified,
        iconSize = 28.dp,
        modifier = modifier
            .buttonShadow()
            .clip(CircleShape)
            .background(shape = CircleShape, color = NekiTheme.colorScheme.white)
            .clickableSingle(onClick = onClick)
            .padding(6.dp),
    )
}

@Composable
internal fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MapCircleButton(
        iconRes = R.drawable.icon_close,
        tint = NekiTheme.colorScheme.gray800,
        iconSize = 24.dp,
        modifier = modifier
            .buttonShadow()
            .clip(CircleShape)
            .background(shape = CircleShape, color = NekiTheme.colorScheme.white)
            .clickableSingle(onClick = onClick)
            .padding(8.dp),
    )
}

@Composable
internal fun CardFavoriteButton(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MapCircleButton(
        iconRes = if (isFavorite) R.drawable.icon_heart_gradient
        else R.drawable.icon_heart_stroked,
        tint = Color.Unspecified,
        iconSize = 22.dp,
        modifier = modifier
            .size(32.dp)
            .background(shape = CircleShape, color = NekiTheme.colorScheme.gray25)
            .clip(CircleShape)
            .clickableSingle(onClick = onClick),
    )
}

@Composable
internal fun CardDirectionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MapCircleButton(
        iconRes = R.drawable.icon_road,
        tint = Color.Unspecified,
        iconSize = 22.dp,
        modifier = modifier
            .size(32.dp)
            .background(shape = CircleShape, color = NekiTheme.colorScheme.gray900)
            .clip(CircleShape)
            .clickableSingle(onClick = onClick),
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
private fun ShowFavoritePhotoBoothButtonOffPreview() {
    NekiTheme {
        ShowFavoritePhotoBoothButton(isFavorite = false)
    }
}

@ComponentPreview
@Composable
private fun ShowFavoritePhotoBoothButtonOnPreview() {
    NekiTheme {
        ShowFavoritePhotoBoothButton(isFavorite = true)
    }
}

@ComponentPreview
@Composable
private fun CloseButtonPreview() {
    NekiTheme {
        CloseButton()
    }
}

@ComponentPreview
@Composable
private fun CardFavoriteButtonOffPreview() {
    NekiTheme {
        CardFavoriteButton(isFavorite = false)
    }
}

@ComponentPreview
@Composable
private fun CardFavoriteButtonOnPreview() {
    NekiTheme {
        CardFavoriteButton(isFavorite = true)
    }
}

@ComponentPreview
@Composable
private fun CardDirectionButtonPreview() {
    NekiTheme {
        CardDirectionButton()
    }
}
