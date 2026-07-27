package com.neki.android.feature.notification.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface NotificationNavKey : NavKey {

    @Serializable
    data object Notification : NotificationNavKey
}

fun MainNavigator.navigateToNotification() {
    navigate(NotificationNavKey.Notification)
}
