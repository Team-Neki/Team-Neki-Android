package com.neki.android.feature.pose.impl.random

import com.neki.android.core.model.Pose
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class RandomPoseUiState(
    val isLoading: Boolean = false,
    val currentPose: Pose = Pose(),
    val randomPoseList: ImmutableList<Pose> = persistentListOf(),
)

sealed interface RandomPoseIntent {
    data object EnterRandomPoseScreen : RandomPoseIntent

    // 튜토리얼
    data object ClickLeftSwipe : RandomPoseIntent
    data object ClickRightSwipe : RandomPoseIntent
    data object ClickStartRandomPose : RandomPoseIntent

    // 기본화면
    data object ClickBackIcon : RandomPoseIntent
    data object ClickCloseIcon : RandomPoseIntent
    data object ClickGoToDetailIcon : RandomPoseIntent
    data object ClickBookmarkIcon : RandomPoseIntent
}

sealed interface RandomPoseEffect {

}
