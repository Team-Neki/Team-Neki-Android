package com.neki.android.feature.pose.impl.main

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PoseState(
    val isLoading: Boolean = false,
    val selectedPeopleCount: PeopleCount? = null,
    val selectedRandomPosePeopleCount: PeopleCount? = null,
    val isShowBookmarkedPose: Boolean = false,
    val bookmarkedPoseList: ImmutableList<Pose> = persistentListOf(),
    val isShowPeopleCountBottomSheet: Boolean = false,
    val isShowRandomPosePeopleCountBottomSheet: Boolean = false,
)

sealed interface PoseIntent {
    data object EnterPoseScreen : PoseIntent
    data object ClickAlarmIcon : PoseIntent
    data object ClickPeopleCountChip : PoseIntent
    data object DismissPeopleCountBottomSheet : PoseIntent
    data object DismissRandomPosePeopleCountBottomSheet : PoseIntent
    data object ClickBookmarkChip : PoseIntent
    data class ClickPoseItem(val item: Pose) : PoseIntent
    data class ClickPeopleCountSheetItem(val peopleCount: PeopleCount) : PoseIntent
    data object ClickRandomPoseRecommendation : PoseIntent
    data class ClickRandomPosePeopleCountSheetItem(val peopleCount: PeopleCount) : PoseIntent
    data object ClickRandomPoseBottomSheetSelectButton : PoseIntent
    data class ClickBookmarkIcon(val pose: Pose) : PoseIntent
    data class BookmarkChanged(val poseId: Long, val isBookmarked: Boolean) : PoseIntent
}

sealed interface PoseEffect {
    data object NavigateToNotification : PoseEffect
    data class NavigateToRandomPose(val peopleCount: PeopleCount) : PoseEffect
    data class NavigateToPoseDetail(val poseId: Long) : PoseEffect
    data class ShowToast(val message: String) : PoseEffect
    data object ScrollToTop : PoseEffect
}
