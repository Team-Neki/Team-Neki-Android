package com.neki.android.core.model

data class Pose(
    val id: Long = 0L,
    val poseImageUrl: String = "",
    val isScrapped: Boolean = false,
    val numberOfPeople: Int = 0,
)
