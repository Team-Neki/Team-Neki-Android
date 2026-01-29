package com.neki.android.core.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

data class PoseState(
    val isLoading: Boolean = false,
    val selectedPeopleCount: PeopleCount? = null,
    val selectedRandomPosePeopleCount: PeopleCount? = null,
    val isShowScrappedPose: Boolean = false,
    val scrappedPoseList: ImmutableList<Pose> = persistentListOf(),
    val isShowPeopleCountBottomSheet: Boolean = false,
    val isShowRandomPosePeopleCountBottomSheet: Boolean = false,
)

sealed interface PoseIntent {
    data object EnterPoseScreen : PoseIntent
    data object ClickAlarmIcon : PoseIntent
    data object ClickPeopleCountChip : PoseIntent
    data object DismissPeopleCountBottomSheet : PoseIntent
    data object DismissRandomPosePeopleCountBottomSheet : PoseIntent
    data object ClickScrapChip : PoseIntent
    data class ClickPoseItem(val item: Pose) : PoseIntent
    data class ClickPeopleCountSheetItem(val peopleCount: PeopleCount) : PoseIntent
    data object ClickRandomPoseRecommendation : PoseIntent
    data class ClickRandomPosePeopleCountSheetItem(val peopleCount: PeopleCount) : PoseIntent
    data object ClickRandomPoseBottomSheetSelectButton : PoseIntent
}

sealed interface PoseEffect {
    data object NavigateToNotification : PoseEffect
    data class NavigateToRandomPose(val peopleCount: PeopleCount) : PoseEffect
    data class NavigateToPoseDetail(val poseId: Long) : PoseEffect
    data class ShowToast(val message: String) : PoseEffect
}

@Serializable
enum class PeopleCount(val displayText: String, val value: Int) {
    ONE("1인", 1),
    TWO("2인", 2),
    THREE("3인", 3),
    FOUR("4인", 4),
    FIVE_OR_MORE("5인 이상", 5),
    ;

    override fun toString(): String = displayText
}
