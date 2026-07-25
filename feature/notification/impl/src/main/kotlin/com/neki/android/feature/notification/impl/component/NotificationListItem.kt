package com.neki.android.feature.notification.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Notification
import com.neki.android.core.ui.compose.VerticalSpacer

@Composable
internal fun NotificationListItem(
    notification: Notification,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = notification.title,
            style = NekiTheme.typography.body16SemiBold,
            color = NekiTheme.colorScheme.gray800,
            overflow = TextOverflow.Ellipsis,
        )
        VerticalSpacer(4.dp)
        Text(
            text = notification.body,
            style = NekiTheme.typography.body14Medium,
            color = NekiTheme.colorScheme.gray500,
        )
        VerticalSpacer(6.dp)
        Text(
            text = notification.createdAt,
            style = NekiTheme.typography.caption12Medium,
            color = NekiTheme.colorScheme.gray300,
        )
    }
}

@ComponentPreview
@Composable
private fun NotificationListItemPreview() {
    NekiTheme {
        NotificationListItem(
            notification = Notification(
                id = 1L,
                type = "ARCHIVE",
                title = "이번 주말 어디서 찍을까요?",
                body = "약속 전에 근처 포토부스를 미리 찾아보세요.",
                link = "neki://archive/123",
                createdAt = "1시간 전",
            ),
        )
    }
}
