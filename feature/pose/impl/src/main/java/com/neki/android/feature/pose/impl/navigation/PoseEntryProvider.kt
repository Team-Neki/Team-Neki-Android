package com.neki.android.feature.pose.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.pose.api.PoseNavKey
import com.neki.android.feature.pose.api.navigateToPoseDetail
import com.neki.android.feature.pose.api.navigateToRandomPose
import com.neki.android.feature.pose.impl.detail.PoseDetailRoute
import com.neki.android.feature.pose.impl.detail.PoseDetailViewModel
import com.neki.android.feature.pose.impl.main.PoseRoute
import com.neki.android.feature.pose.impl.random.RandomPoseRoute
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
    entry<PoseNavKey.Main> {
        PoseRoute(
            navigateToPoseDetail = navigator::navigateToPoseDetail,
            navigateToRandomPose = navigator::navigateToRandomPose,
            navigateToNotification = {},
        )
    }

    entry<PoseNavKey.Detail> { key ->
        PoseDetailRoute(
            viewModel = hiltViewModel<PoseDetailViewModel, PoseDetailViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.pose)
                },
            ),
            navigateBack = navigator::goBack,
        )
    }

    entry<PoseNavKey.RandomPose> {
        RandomPoseRoute(
            navigateBack = navigator::goBack,
            navigateToPoseDetail = navigator::navigateToPoseDetail,
        )
    }
}
