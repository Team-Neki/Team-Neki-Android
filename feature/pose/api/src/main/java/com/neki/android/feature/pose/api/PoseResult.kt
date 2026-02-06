package com.neki.android.feature.pose.api

sealed interface PoseResult {
    data class ScrapChanged(val poseId: Long, val isScrapped: Boolean) : PoseResult
}
