package com.neki.android.feature.map.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.NavigatorImpl
import com.neki.android.feature.map.api.MapNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureAModule {

    @IntoSet
    @Provides
    fun provideFeatureAEntryBuilder(navigatorImpl: NavigatorImpl): EntryProviderInstaller = {
        mapEntry()
    }
}

private fun EntryProviderScope<NavKey>.mapEntry() {
    entry<MapNavKey.Map> {}
}


