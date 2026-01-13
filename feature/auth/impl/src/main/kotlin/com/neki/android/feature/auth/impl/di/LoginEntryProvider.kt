package com.neki.android.feature.auth.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.auth.api.LoginNavKey
import com.neki.android.feature.auth.impl.LoginRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginEntryProvider {

    @IntoSet
    @Provides
    fun provideLoginEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        loginEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.loginEntry(navigator: Navigator) {
    entry<LoginNavKey.Login> {
        LoginRoute(
            navigateBack = navigator::goBack,
        )
    }
}
