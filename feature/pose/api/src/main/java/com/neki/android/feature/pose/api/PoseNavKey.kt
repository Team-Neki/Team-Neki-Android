package com.neki.android.feature.pose.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface PoseNavKey : NavKey {

    @Serializable
    data object Pose : PoseNavKey
}

fun Navigator.navigateToPose() {
    navigate(PoseNavKey.Pose)
}

