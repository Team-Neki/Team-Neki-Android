package com.neki.android.core.designsystem.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.neki.android.core.designsystem.R

internal val pretendardFamily = FontFamily(
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_regular, FontWeight.Normal),
)

private val pretendardStyle = TextStyle(
    fontFamily = pretendardFamily,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false,
    ),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

internal val defaultNekiTypography = NekiTypography(
    // Title 24
    title24Bold = pretendardStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.02).em,
    ),
    title24SemiBold = pretendardStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    // Title 20
    title20Bold = pretendardStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.02).em,
    ),
    title20SemiBold = pretendardStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.02).em,
    ),
    title20Medium = pretendardStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.02).em,
    ),
    // Title 18
    title18Bold = pretendardStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.02).em,
    ),
    title18SemiBold = pretendardStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.02).em,
    ),
    title18Medium = pretendardStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.02).em,
    ),
    title18Regular = pretendardStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.02).em,
    ),
    // Body 16
    body16SemiBold = pretendardStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.02).em,
    ),
    body16Medium = pretendardStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.02).em,
    ),
    body16Regular = pretendardStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.02).em,
    ),
    // Body 14
    body14SemiBold = pretendardStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.02).em,
    ),
    body14Medium = pretendardStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.02).em,
    ),
    body14Regular = pretendardStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.02).em,
    ),
    // Caption 12
    caption12SemiBold = pretendardStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.02).em,
    ),
    caption12Medium = pretendardStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.02).em,
    ),
    caption12Regular = pretendardStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.02).em,
    ),
    // Caption 11
    caption11SemiBold = pretendardStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.02).em,
    ),
    caption11Medium = pretendardStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.02).em,
    ),
)

@Immutable
data class NekiTypography(
    // Title 24
    val title24Bold: TextStyle,
    val title24SemiBold: TextStyle,
    // Title 20
    val title20Bold: TextStyle,
    val title20SemiBold: TextStyle,
    val title20Medium: TextStyle,
    // Title 18
    val title18Bold: TextStyle,
    val title18SemiBold: TextStyle,
    val title18Medium: TextStyle,
    val title18Regular: TextStyle,
    // Body 16
    val body16SemiBold: TextStyle,
    val body16Medium: TextStyle,
    val body16Regular: TextStyle,
    // Body 14
    val body14SemiBold: TextStyle,
    val body14Medium: TextStyle,
    val body14Regular: TextStyle,
    // Caption 12
    val caption12SemiBold: TextStyle,
    val caption12Medium: TextStyle,
    val caption12Regular: TextStyle,
    // Caption 11
    val caption11SemiBold: TextStyle,
    val caption11Medium: TextStyle,
)

val LocalTypography = staticCompositionLocalOf { defaultNekiTypography }
