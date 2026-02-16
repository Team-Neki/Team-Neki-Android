package com.neki.android.feature.map.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.feature.map.api.MapNavKey
import com.neki.android.feature.map.impl.MapRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object MapEntryProviderModule {

    @IntoSet
    @Provides
    fun provideMapEntryBuilder(navigator: MainNavigator): EntryProviderInstaller = {
        mapEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.mapEntry(navigator: MainNavigator) {
    entry<MapNavKey.Map> {
        MapRoute()
    }
}
