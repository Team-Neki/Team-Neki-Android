package com.neki.android.core.designsystem.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NekiColorScheme(
    // System
    val white: Color,

    // Grayscale
    val gray900: Color,
    val gray800: Color,
    val gray700: Color,
    val gray600: Color,
    val gray500: Color,
    val gray400: Color,
    val gray300: Color,
    val gray200: Color,
    val gray100: Color,
    val gray75: Color,
    val gray50: Color,
    val gray25: Color,

    // Primary
    val primary900: Color,
    val primary800: Color,
    val primary700: Color,
    val primary600: Color,
    val primary500: Color,
    val primary400: Color,
    val primary300: Color,
    val primary200: Color,
    val primary100: Color,
    val primary50: Color,
    val primary25: Color,

    // Album Cover
    val favoriteAlbumCover: Color,
    val defaultAlbumCover: Color,
)

internal val defaultNekiColors = NekiColorScheme(
    // System
    white = Color(0xFFFFFFFF),

    // Grayscale
    gray900 = Color(0xFF202227),
    gray800 = Color(0xFF3C3E48),
    gray700 = Color(0xFF4F525F),
    gray600 = Color(0xFF616575),
    gray500 = Color(0xFF74788B),
    gray400 = Color(0xFF8A8E9E),
    gray300 = Color(0xFFA0A3B0),
    gray200 = Color(0xFFB7B9C3),
    gray100 = Color(0xFFCDCED5),
    gray75 = Color(0xFFE3E4E8),
    gray50 = Color(0xFFEEF1F1),
    gray25 = Color(0xFFF9FAFA),

    // Primary
    primary900 = Color(0xFF7A0A00),
    primary800 = Color(0xFFA30E00),
    primary700 = Color(0xFFCC1100),
    primary600 = Color(0xFFF51500),
    primary500 = Color(0xFFFF311F),
    primary400 = Color(0xFFFF5647),
    primary300 = Color(0xFFFF786B),
    primary200 = Color(0xFFFFA299),
    primary100 = Color(0xFFFFC7C2),
    primary50 = Color(0xFFFFDAD6),
    primary25 = Color(0xFFFFECEB),

    // Album Cover
    favoriteAlbumCover = Color(0xFFFF5647),
    defaultAlbumCover = Color(0xFF202227),
)

val LocalColorScheme = staticCompositionLocalOf { defaultNekiColors }
