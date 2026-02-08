package com.neki.android.core.designsystem.logo

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
private fun NekiAppLogo(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.icon_neki_logo_white),
        contentDescription = null,
        tint = color,
        modifier = modifier,
    )
}

@Composable
fun WhiteNekiAppLogo(
    modifier: Modifier = Modifier,
) {
    NekiAppLogo(
        color = NekiTheme.colorScheme.white,
        modifier = modifier,
    )
}

@Composable
fun PrimaryNekiAppLogo(
    modifier: Modifier = Modifier,
) {
    NekiAppLogo(
        color = NekiTheme.colorScheme.primary400,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun WhiteNekiAppLogoPreview() {
    NekiTheme {
        WhiteNekiAppLogo()
    }
}

@Preview
@Composable
private fun PrimaryNekiAppLogoPreview() {
    NekiTheme {
        PrimaryNekiAppLogo()
    }
}
