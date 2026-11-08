package com.neki.android.feature.pose.impl.main

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import com.neki.android.feature.pose.impl.const.PoseConst.TEMP_TOTAL_POSE_COUNT
import com.neki.android.feature.pose.api.PoseNavKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PoseState(
    val isLoading: Boolean = false,
    val selectedPeopleCount: PeopleCount? = null,
    val selectedRelationTag: PoseRelationTag? = null,
    val selectedSortType: PoseSortType = PoseSortType.LATEST,
    val totalPoseCount: Int? = TEMP_TOTAL_POSE_COUNT,
    val selectedRandomPosePeopleCount: PeopleCount? = null,
    val isShowBookmarkedPose: Boolean = false,
    val bookmarkedPoseList: ImmutableList<Pose> = persistentListOf(),
    val isShowPeopleCountBottomSheet: Boolean = false,
    val isShowRandomPosePeopleCountBottomSheet: Boolean = false,
    val isShowSortDropdown: Boolean = false,
)

enum class PoseRelationTag(val displayText: String) {
    FRIEND("친구"),
    COUPLE("커플"),
    CRUSH("썸"),
}

enum class PoseSortType(val displayText: String) {
    POPULAR("인기순"),
    LATEST("최신순"),
    RECOMMENDED("추천순"),
}

sealed interface PoseIntent {
    data object EnterPoseScreen : PoseIntent
    data object ClickAlarmIcon : PoseIntent
    data object ClickQRScanIcon : PoseIntent
    data object ClickPeopleCountChip : PoseIntent
    data object DismissPeopleCountBottomSheet : PoseIntent
    data object DismissRandomPosePeopleCountBottomSheet : PoseIntent
    data object ClickBookmarkChip : PoseIntent
    data class ClickRelationTagChip(val relationTag: PoseRelationTag) : PoseIntent
    data object ClickSortDropdown : PoseIntent
    data object DismissSortDropdown : PoseIntent
    data class ClickSortDropdownItem(val sortType: PoseSortType) : PoseIntent
    data class ClickPoseItem(val item: Pose, val index: Int) : PoseIntent
    data class ClickPeopleCountSheetItem(val peopleCount: PeopleCount) : PoseIntent
    data object ClickRandomPoseRecommendation : PoseIntent
    data class ClickRandomPosePeopleCountSheetItem(val peopleCount: PeopleCount) : PoseIntent
    data object ClickRandomPoseBottomSheetSelectButton : PoseIntent
    data class ClickBookmarkIcon(val pose: Pose) : PoseIntent
    data class BookmarkChanged(val poseId: Long, val isBookmarked: Boolean) : PoseIntent
}

sealed interface PoseEffect {
    data object NavigateToNotification : PoseEffect
    data object NavigateToQRScan : PoseEffect
    data class NavigateToRandomPose(val peopleCount: PeopleCount) : PoseEffect
    data class NavigateToPoseDetail(val poseDetail: PoseNavKey.PoseDetail) : PoseEffect
    data class ShowToast(val message: String) : PoseEffect
    data object ScrollToTop : PoseEffect
}
