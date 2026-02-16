package com.neki.android.feature.pose.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface PoseNavKey : NavKey {

    @Serializable
    data object PoseMain : PoseNavKey

    @Serializable
    data class RandomPose(val peopleCount: PeopleCount) : PoseNavKey

    @Serializable
    data class PoseDetail(val poseId: Long) : PoseNavKey
}

fun MainNavigator.navigateToPose() {
    navigate(PoseNavKey.PoseMain)
}

fun MainNavigator.navigateToRandomPose(peopleCount: PeopleCount) {
    navigate(PoseNavKey.RandomPose(peopleCount))
}

fun MainNavigator.navigateToPoseDetail(poseId: Long) {
    navigate(PoseNavKey.PoseDetail(poseId))
}
