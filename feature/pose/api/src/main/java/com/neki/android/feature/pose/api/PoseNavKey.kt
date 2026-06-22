package com.neki.android.feature.pose.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.core.model.SortOrder
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface PoseNavKey : NavKey {

    @Serializable
    data object PoseMain : PoseNavKey

    @Serializable
    data class RandomPose(val peopleCount: PeopleCount) : PoseNavKey

    @Serializable
    data class PoseDetail(
        val poseId: Long,
        val poses: List<Pose> = emptyList(),
        val initialIndex: Int = 0,
        val hasNext: Boolean = false,
        val headCount: PeopleCount? = null,
        val sortOrder: SortOrder = SortOrder.DESC,
        val isBookmarkOnly: Boolean = false,
    ) : PoseNavKey
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

fun MainNavigator.navigateToPoseDetail(poseDetail: PoseNavKey.PoseDetail) {
    navigate(poseDetail)
}
