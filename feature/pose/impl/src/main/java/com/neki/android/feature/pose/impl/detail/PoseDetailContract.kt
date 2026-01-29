package com.neki.android.feature.pose.impl.detail

import com.neki.android.core.model.Pose

data class PoseDetailState(
    val isLoading: Boolean = false,
    val pose: Pose = Pose(),
    val committedScrap: Boolean = false,
)

sealed interface PoseDetailIntent {
    data object EnterPoseDetailScreen : PoseDetailIntent
    data object ClickBackIcon : PoseDetailIntent
    data object ClickScrapIcon : PoseDetailIntent
    data class ScrapCommitted(val newScrap: Boolean) : PoseDetailIntent
    data class RevertScrap(val originalScrap: Boolean) : PoseDetailIntent
}

sealed interface PoseDetailSideEffect {
    data object NavigateBack : PoseDetailSideEffect
    data class ShowToast(val message: String) : PoseDetailSideEffect
}
