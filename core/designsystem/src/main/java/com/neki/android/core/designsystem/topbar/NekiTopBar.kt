package com.neki.android.core.designsystem.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NekiTopBar(
    modifier: Modifier = Modifier,
    title: @Composable ((Modifier) -> Unit)? = null,
    leadingIcon: @Composable ((Modifier) -> Unit)? = null,
    actions: @Composable ((Modifier) -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 20.dp),
    ) {
        leadingIcon?.invoke(Modifier.align(Alignment.CenterStart))
        title?.invoke(Modifier.align(Alignment.Center))
        actions?.invoke(Modifier.align(Alignment.CenterEnd))
    }
}
