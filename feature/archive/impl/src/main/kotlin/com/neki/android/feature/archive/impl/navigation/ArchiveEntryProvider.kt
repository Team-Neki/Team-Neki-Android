package com.neki.android.feature.archive.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.archive.api.ArchiveNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object ArchiveEntryProviderModule {

    @IntoSet
    @Provides
    fun provideArchiveEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        archiveEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.archiveEntry(navigator: Navigator) {
    entry<ArchiveNavKey.Archive> {
        navigator
    }
}
