package com.neki.android.feature.notification.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.feature.notification.api.NotificationNavKey
import com.neki.android.feature.notification.impl.NotificationRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object NotificationEntryProviderModule {

    @IntoSet
    @Provides
    fun provideNotificationEntryBuilder(navigator: MainNavigator): EntryProviderInstaller = {
        notificationEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.notificationEntry(navigator: MainNavigator) {
    entry<NotificationNavKey.Notification> {
        NotificationRoute(navigateBack = navigator::goBack)
    }
}
