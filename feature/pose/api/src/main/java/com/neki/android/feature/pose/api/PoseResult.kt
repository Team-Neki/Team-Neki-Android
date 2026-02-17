package com.neki.android.feature.pose.api

sealed interface PoseResult {
    data class BookmarkChanged(val poseId: Long, val isBookmarked: Boolean) : PoseResult
}
