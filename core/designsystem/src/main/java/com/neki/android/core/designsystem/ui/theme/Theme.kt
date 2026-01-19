package com.neki.android.core.designsystem.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun ProvideTypographyAndColor(
    typography: NekiTypography,
    colors: NekiColorScheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTypography provides typography,
        LocalColorScheme provides colors,
        content = content,
    )
}

@Composable
fun NekiTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = LocalDensity.current.density,
            fontScale = 1f,
        ),
    ) {
        ProvideTypographyAndColor(
            typography = defaultNekiTypography,
            colors = defaultNekiColors,
        ) {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    background = NekiTheme.colorScheme.white,
                ),
                content = content,
            )
        }
    }
}

object NekiTheme {
    val typography: NekiTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val colorScheme: NekiColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current
}
