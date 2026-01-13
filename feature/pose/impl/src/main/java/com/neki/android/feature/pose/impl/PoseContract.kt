package com.neki.android.feature.pose.impl

import com.neki.android.core.model.Pose
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PoseState(
    val isLoading: Boolean = false,
    val selectedNumberOfPeople: NumberOfPeople = NumberOfPeople.UNSELECTED,
    val showScrappedPose: Boolean = false,
    val randomPoseList: ImmutableList<Pose> = persistentListOf(),
    val scrappedPoseList: ImmutableList<Pose> = persistentListOf(),
    val selectedPose: Pose = Pose(),
    val showNumberOfPeopleBottomSheet: Boolean = false,
) {
    companion object {
        val dummy = persistentListOf(
            "https://images.unsplash.com/photo-1474511320723-9a56873571b7",
            "https://images.unsplash.com/photo-1437622368342-7a3d73a34c8f",
            "https://images.unsplash.com/photo-1425082661705-1834bfd09dca",
            "https://images.unsplash.com/photo-1484406566174-9da000fda645",
            "https://images.unsplash.com/photo-1456926631375-92c8ce872def",
            "https://images.unsplash.com/photo-1474314243412-cd4a79534427",
            "https://images.unsplash.com/photo-1517849845537-4d257902454a",
            "https://images.unsplash.com/photo-1518791841217-8f162f1e1131",
            "https://images.unsplash.com/photo-1543466835-00a7907e9de1",
            "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba",
            "https://images.unsplash.com/photo-1452857297128-d9c29adba80b",
        )
    }
}

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
