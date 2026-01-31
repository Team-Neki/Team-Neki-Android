package com.neki.android.core.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
//@Immutable
data class Pose(
    val id: Long = 0L,
    val poseImageUrl: String = "",
    val isScrapped: Boolean = false,
    val peopleCount: Int = 0,
)
