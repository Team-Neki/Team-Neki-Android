package com.neki.android.feature.notification.impl

import com.neki.android.core.model.Notification
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class NotificationState(
    val isLoading: Boolean = true,
    val notifications: ImmutableList<Notification> = persistentListOf(),
)

sealed interface NotificationIntent {
    data object EnterNotificationScreen : NotificationIntent
    data object ClickBack : NotificationIntent
    data class ClickNotification(val id: Long) : NotificationIntent
}

sealed interface NotificationEffect {
    data object NavigateBack : NotificationEffect
}
