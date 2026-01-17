package com.neki.android.core.designsystem.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ui.theme.NekiTheme

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
            .padding(vertical = 13.dp)
            .padding(horizontal = 20.dp),
    ) {
        leadingIcon?.invoke(Modifier.align(Alignment.CenterStart))
        title?.invoke(Modifier.align(Alignment.Center))
        actions?.invoke(Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun NekiTitleTopBar(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable ((Modifier) -> Unit)? = null,
    actions: @Composable ((Modifier) -> Unit)? = null,
) {
    NekiTopBar(
        modifier = modifier,
        leadingIcon = leadingIcon,
        actions = actions,
        title = { modifier ->
            Text(
                modifier = modifier,
                text = title,
                style = NekiTheme.typography.title18SemiBold,
                color = NekiTheme.colorScheme.gray900,
            )
        },
    )
}

@Composable
fun NekiLeftTitleTopBar(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        title?.invoke()
        actions?.invoke()
    }
}
