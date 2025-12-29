package com.neki.android.feature.pose.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.NavigatorImpl
import com.neki.android.feature.pose.api.PoseNavKey
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
        poseEntry()
    }
}

private fun EntryProviderScope<NavKey>.poseEntry() {
    entry<PoseNavKey.Pose> {}
}


