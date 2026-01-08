package com.neki.android.core.ui.compose

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalSpacer(
    width: Dp = 0.dp,
) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun RowScope.HorizontalSpacer(
    weight: Float,
) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun VerticalSpacer(
    height: Dp = 0.dp,
) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun ColumnScope.VerticalSpacer(
    weight: Float,
) {
    Spacer(modifier = Modifier.weight(weight))
}
