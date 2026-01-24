package com.neki.android.feature.pose.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface PoseNavKey : NavKey {

    @Serializable
    data object Main : PoseNavKey

    @Serializable
    data class RandomPose(val peopleCount: PeopleCount) : PoseNavKey

    @Serializable
    data class Detail(val pose: Pose) : PoseNavKey
}

fun Navigator.navigateToPose() {
    navigate(PoseNavKey.Main)
}

fun Navigator.navigateToRandomPose(peopleCount: PeopleCount) {
    navigate(PoseNavKey.RandomPose(peopleCount))
}

fun Navigator.navigateToPoseDetail(pose: Pose) {
    navigate(PoseNavKey.Detail(pose))
}
