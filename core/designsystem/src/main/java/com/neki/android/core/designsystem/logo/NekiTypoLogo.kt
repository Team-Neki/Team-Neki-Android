package com.neki.android.core.designsystem.logo

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
private fun NekiTypoLogo(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.icon_neki_logo_typo),
        contentDescription = null,
        tint = color,
        modifier = modifier,
    )
}

@Composable
fun PrimaryNekiTypoLogo(
    modifier: Modifier = Modifier,
) {
    NekiTypoLogo(
        color = NekiTheme.colorScheme.primary400,
        modifier = modifier,
    )
}

@Composable
fun GrayNekiTypoLogo(
    modifier: Modifier = Modifier,
) {
    NekiTypoLogo(
        color = NekiTheme.colorScheme.gray900,
        modifier = modifier,
    )
}

@Composable
fun WhiteNekiTypoLogo(
    modifier: Modifier = Modifier,
) {
    NekiTypoLogo(
        color = NekiTheme.colorScheme.white,
        modifier = modifier,
    )
}

@ComponentPreview
@Composable
private fun PrimaryNekiTypoLogoPreview() {
    NekiTheme {
        PrimaryNekiTypoLogo()
    }
}

@ComponentPreview
@Composable
private fun GrayNekiTypoLogoPreview() {
    NekiTheme {
        GrayNekiTypoLogo()
    }
}

@Preview
@Composable
private fun WhiteNekiTypoLogoPreview() {
    NekiTheme {
        WhiteNekiTypoLogo()
    }
}
