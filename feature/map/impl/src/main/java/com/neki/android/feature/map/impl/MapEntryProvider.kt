package com.neki.android.feature.map.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.map.api.MapNavKey
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
    fun provideMapEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        mapEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.mapEntry(navigator: Navigator) {
    entry<MapNavKey.Map> {
        navigator
    }
}
