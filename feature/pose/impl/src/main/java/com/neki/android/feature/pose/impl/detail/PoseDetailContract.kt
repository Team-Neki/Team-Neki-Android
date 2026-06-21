package com.neki.android.feature.pose.impl.detail

import com.neki.android.core.model.Pose

data class PoseDetailState(
    val isLoading: Boolean = false,
    val poses: List<Pose> = emptyList(),
    val currentPage: Int = 0,
) {
    val currentIndex: Int get() = if (poses.isEmpty()) 0 else currentPage.coerceIn(0, poses.lastIndex)
    val pose: Pose get() = poses.getOrElse(currentIndex) { Pose() }
}

sealed interface PoseDetailIntent {
    data object EnterPoseDetailScreen : PoseDetailIntent
    data object ClickBackIcon : PoseDetailIntent
    data class PageChanged(val page: Int) : PoseDetailIntent
    data object ClickBookmarkIcon : PoseDetailIntent
    data class BookmarkCommitted(val poseId: Long, val newBookmark: Boolean) : PoseDetailIntent
    data class RevertBookmark(val poseId: Long, val originalBookmark: Boolean) : PoseDetailIntent
}

sealed interface PoseDetailSideEffect {
    data object NavigateBack : PoseDetailSideEffect
    data class ShowToast(val message: String) : PoseDetailSideEffect
    data class NotifyBookmarkChanged(val poseId: Long, val isBookmarked: Boolean) : PoseDetailSideEffect
}
