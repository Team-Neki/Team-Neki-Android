package com.neki.android.feature.pose.impl

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PoseState(
    val isLoading: Boolean = false,
    val selectedNumberOfPeople: NumberOfPeople = NumberOfPeople.UNSELECTED,
    val showScrappedPose: Boolean = false,
    val randomPoseList: ImmutableList<String> = persistentListOf(),
    val scrappedPoseList: ImmutableList<String> = persistentListOf(),
    val selectedPose: String = "",
)

sealed interface PoseIntent {
    // Pose Main
    data object ClickAlarmIcon : PoseIntent
    data object ClickNumberOfPeople : PoseIntent
    data object ClickScrap : PoseIntent
    data class ClickPoseItem(val imageUrl: String) : PoseIntent
    data object ClickRandomPoseRecommendation : PoseIntent
    data class ClickNumberOfPeopleSheetItem(val numberOfPeople: NumberOfPeople) : PoseIntent

    // Pose Detail
    data object ClickBackIcon : PoseIntent
    data object ClickScrapIcon : PoseIntent
}

sealed interface PoseEffect {
    data class ShowToast(val message: String) : PoseEffect
    data object NavigateToPoseDetail : PoseEffect
}

enum class NumberOfPeople(val displayText: String, val value: Int) {
    UNSELECTED("인원수", 0),
    ONE("1인", 1),
    TWO("2인", 2),
    THREE("3인", 3),
    FOUR("4인", 4),
    FIVE_OR_MORE("5인 이상", 5),
}
