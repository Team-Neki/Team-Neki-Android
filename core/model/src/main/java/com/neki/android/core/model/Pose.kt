package com.neki.android.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Pose(
    val id: Long = 0L,
    val poseImageUrl: String = "",
    val isScrapped: Boolean = false,
    val peopleCount: Int = 0,
)
