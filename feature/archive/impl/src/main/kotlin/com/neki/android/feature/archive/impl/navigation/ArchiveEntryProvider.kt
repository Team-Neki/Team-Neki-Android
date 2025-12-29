package navigation.com.neki.android.feature.archive.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.NavigatorImpl
import com.neki.android.feature.archive.com.neki.android.feature.archive.api.ArchiveNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureArchiveModule {

    @IntoSet
    @Provides
    fun provideFeatureArchiveEntryBuilder(navigatorImpl: NavigatorImpl): EntryProviderInstaller = {
        archiveEntry()
    }
}

private fun EntryProviderScope<NavKey>.archiveEntry() {
    entry<ArchiveNavKey.Archive> {}
}
