package com.neki.android.feature.pose.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.HiltSharedViewModelStoreNavEntryDecorator
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.pose.api.PoseNavKey
import com.neki.android.feature.pose.api.navigateToPoseDetail
import com.neki.android.feature.pose.api.navigateToRandomPose
import com.neki.android.feature.pose.impl.detail.PoseDetailRoute
import com.neki.android.feature.pose.impl.main.PoseRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object PoseEntryProviderModule {

    @IntoSet
    @Provides
    fun providePoseEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        poseEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.poseEntry(navigator: Navigator) {
    entry<PoseNavKey.Pose>(
        clazzContentKey = { key -> key.toString() },
    ) {
        PoseRoute(
            navigateToPoseDetail = navigator::navigateToPoseDetail,
            navigateToNotification = {},
        )
    }

    entry<PoseNavKey.Detail>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            PoseNavKey.Pose.toString(),
        ),
    ) {
        PoseDetailRoute(
            navigateBack = navigator::goBack,
            navigateToRandomPose = navigator::navigateToRandomPose,
        )
    }
}
