package com.neki.android.feature.pose.impl.random

import com.neki.android.core.model.Pose
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class RandomPoseUiState(
    val isLoading: Boolean = false,
    val isShowTutorial: Boolean = true,
    val currentIndex: Int = 0,
    val poseList: ImmutableList<Pose> = persistentListOf(),
    val committedScraps: Map<Long, Boolean> = emptyMap(),
) {
    val currentPose: Pose?
        get() = poseList.getOrNull(currentIndex)

    val hasPrevious: Boolean
        get() = currentIndex > 0
}

internal sealed class FetchPoseResult(val tryCount: Int) {
    class Success(tryCount: Int, val pose: Pose) : FetchPoseResult(tryCount)
    class Duplicated(tryCount: Int) : FetchPoseResult(tryCount)
    class Failure(tryCount: Int, val throwable: Throwable) : FetchPoseResult(tryCount)
}

sealed interface RandomPoseIntent {
    data object EnterRandomPoseScreen : RandomPoseIntent

    // 튜토리얼
    data object ClickStartRandomPose : RandomPoseIntent

    // 기본화면
    data object ClickCloseIcon : RandomPoseIntent
    data object ClickGoToDetailIcon : RandomPoseIntent
    data object ClickScrapIcon : RandomPoseIntent
    data object ClickLeftSwipe : RandomPoseIntent
    data object ClickRightSwipe : RandomPoseIntent
}

sealed interface RandomPoseEffect {
    data object NavigateBack : RandomPoseEffect
    data class NavigateToDetail(val poseId: Long) : RandomPoseEffect
    data class SwipePoseImage(val index: Int) : RandomPoseEffect
    data class ShowToast(val message: String) : RandomPoseEffect
}
