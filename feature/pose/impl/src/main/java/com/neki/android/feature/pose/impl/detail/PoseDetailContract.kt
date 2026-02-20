package com.neki.android.feature.pose.impl.detail

import com.neki.android.core.model.Pose

data class PoseDetailState(
    val isLoading: Boolean = false,
    val pose: Pose = Pose(),
    val committedBookmark: Boolean = false,
)

sealed interface PoseDetailIntent {
    data object EnterPoseDetailScreen : PoseDetailIntent
    data object ClickBackIcon : PoseDetailIntent
    data object ClickBookmarkIcon : PoseDetailIntent
    data class BookmarkCommitted(val newBookmark: Boolean) : PoseDetailIntent
    data class RevertBookmark(val originalBookmark: Boolean) : PoseDetailIntent
}

sealed interface PoseDetailSideEffect {
    data object NavigateBack : PoseDetailSideEffect
    data class ShowToast(val message: String) : PoseDetailSideEffect
    data class NotifyBookmarkChanged(val poseId: Long, val isBookmarked: Boolean) : PoseDetailSideEffect
}
