package com.neki.android.feature.notification.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun NotificationTopBar(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
) {
    BackTitleTopBar(
        modifier = modifier,
        title = "알림",
        onBack = onClickBack,
    )
}

@ComponentPreview
@Composable
private fun NotificationTopBarPreview() {
    NekiTheme {
        NotificationTopBar()
    }
}
