package com.neki.android.feature.pose.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface PoseNavKey : NavKey {

    @Serializable
    data object Pose : PoseNavKey

    @Serializable
    data class RandomPose(val peopleCount: PeopleCount) : PoseNavKey

    @Serializable
    data object Detail : PoseNavKey
}

fun Navigator.navigateToPose() {
    navigate(PoseNavKey.Pose)
}

fun Navigator.navigateToRandomPose(peopleCount: PeopleCount) {
    navigate(PoseNavKey.RandomPose(peopleCount))
}

fun Navigator.navigateToPoseDetail() {
    navigate(PoseNavKey.Detail)
}
