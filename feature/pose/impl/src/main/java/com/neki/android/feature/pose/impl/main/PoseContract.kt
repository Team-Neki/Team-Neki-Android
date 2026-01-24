package com.neki.android.feature.pose.impl.main

import com.neki.android.core.model.Pose
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val dummyPoseList = persistentListOf(
    Pose(id = 1, poseImageUrl = "https://picsum.photos/seed/poseA/400/520", numberOfPeople = 1),
    Pose(id = 2, poseImageUrl = "https://picsum.photos/seed/poseB/400/680", numberOfPeople = 2),
    Pose(id = 3, poseImageUrl = "https://picsum.photos/seed/poseC/400/450", numberOfPeople = 1),
    Pose(id = 4, poseImageUrl = "https://picsum.photos/seed/poseD/400/600", numberOfPeople = 3),
    Pose(id = 5, poseImageUrl = "https://picsum.photos/seed/poseE/400/550", numberOfPeople = 2),
    Pose(id = 6, poseImageUrl = "https://picsum.photos/seed/poseF/400/720", numberOfPeople = 4),
    Pose(id = 7, poseImageUrl = "https://picsum.photos/seed/poseG/400/480", numberOfPeople = 1),
    Pose(id = 8, poseImageUrl = "https://picsum.photos/seed/poseH/400/650", numberOfPeople = 2),
    Pose(id = 9, poseImageUrl = "https://picsum.photos/seed/poseI/400/500", numberOfPeople = 3),
    Pose(id = 10, poseImageUrl = "https://picsum.photos/seed/poseJ/400/580", numberOfPeople = 1),
    Pose(id = 11, poseImageUrl = "https://picsum.photos/seed/poseK/400/700", numberOfPeople = 5),
    Pose(id = 12, poseImageUrl = "https://picsum.photos/seed/poseL/400/460", numberOfPeople = 2),
    Pose(id = 13, poseImageUrl = "https://picsum.photos/seed/poseM/400/620", numberOfPeople = 1),
    Pose(id = 14, poseImageUrl = "https://picsum.photos/seed/poseN/400/540", numberOfPeople = 4),
    Pose(id = 15, poseImageUrl = "https://picsum.photos/seed/poseO/400/690", numberOfPeople = 2),
    Pose(id = 16, poseImageUrl = "https://picsum.photos/seed/poseP/400/470", numberOfPeople = 3),
    Pose(id = 17, poseImageUrl = "https://picsum.photos/seed/poseQ/400/610", numberOfPeople = 1),
    Pose(id = 18, poseImageUrl = "https://picsum.photos/seed/poseR/400/530", numberOfPeople = 2),
    Pose(id = 19, poseImageUrl = "https://picsum.photos/seed/poseS/400/670", numberOfPeople = 5),
    Pose(id = 20, poseImageUrl = "https://picsum.photos/seed/poseT/400/490", numberOfPeople = 1),
    Pose(id = 21, poseImageUrl = "https://picsum.photos/seed/poseU/400/640", numberOfPeople = 2),
    Pose(id = 22, poseImageUrl = "https://picsum.photos/seed/poseV/400/560", numberOfPeople = 3),
)

private val scrappedDummyList = persistentListOf(
    Pose(id = 101, poseImageUrl = "https://picsum.photos/seed/scrapA/400/520", isScrapped = true, numberOfPeople = 1),
    Pose(id = 102, poseImageUrl = "https://picsum.photos/seed/scrapB/400/680", isScrapped = true, numberOfPeople = 2),
    Pose(id = 103, poseImageUrl = "https://picsum.photos/seed/scrapC/400/450", isScrapped = true, numberOfPeople = 1),
    Pose(id = 104, poseImageUrl = "https://picsum.photos/seed/scrapD/400/600", isScrapped = true, numberOfPeople = 3),
    Pose(id = 105, poseImageUrl = "https://picsum.photos/seed/scrapE/400/550", isScrapped = true, numberOfPeople = 2),
    Pose(id = 106, poseImageUrl = "https://picsum.photos/seed/scrapF/400/720", isScrapped = true, numberOfPeople = 4),
    Pose(id = 107, poseImageUrl = "https://picsum.photos/seed/scrapG/400/480", isScrapped = true, numberOfPeople = 1),
)

data class PoseState(
    val isLoading: Boolean = false,
    val selectedNumberOfPeople: NumberOfPeople = NumberOfPeople.UNSELECTED,
    val isShowScrappedPose: Boolean = false,
    val randomPoseList: ImmutableList<Pose> = dummyPoseList,
    val scrappedPoseList: ImmutableList<Pose> = scrappedDummyList,
    val selectedPose: Pose = Pose(),
    val isShowNumberOfPeopleBottomSheet: Boolean = false,
)

sealed interface PoseIntent {
    // Pose Main
    data object EnterPoseScreen : PoseIntent
    data object ClickAlarmIcon : PoseIntent
    data object ClickNumberOfPeopleChip : PoseIntent
    data object DismissNumberOfPeopleBottomSheet : PoseIntent
    data object ClickScrapChip : PoseIntent
    data class ClickPoseItem(val item: Pose) : PoseIntent
    data object ClickRandomPoseRecommendation : PoseIntent
    data class ClickNumberOfPeopleSheetItem(val numberOfPeople: NumberOfPeople) : PoseIntent

    // Pose Detail
    data object ClickBackIcon : PoseIntent
    data object ClickScrapIcon : PoseIntent
}

sealed interface PoseEffect {
    data object NavigateBack : PoseEffect
    data object NavigateToNotification : PoseEffect
    data object NavigateToPoseDetail : PoseEffect
    data class ShowToast(val message: String) : PoseEffect
}

enum class NumberOfPeople(val displayText: String, val value: Int) {
    UNSELECTED("인원수", 0),
    ONE("1인", 1),
    TWO("2인", 2),
    THREE("3인", 3),
    FOUR("4인", 4),
    FIVE_OR_MORE("5인 이상", 5),
}
