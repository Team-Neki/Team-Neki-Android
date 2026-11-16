package com.neki.android.feature.pose.impl.main

import com.neki.android.core.model.PeopleCount

internal fun PoseState.toggleBookmarkFilter(): PoseState = copy(
    isShowBookmarkedPose = !isShowBookmarkedPose,
    selectedPeopleCount = null,
    selectedRelationTag = null,
)

internal fun PoseState.togglePeopleCountFilter(peopleCount: PeopleCount): PoseState {
    val newPeopleCount = peopleCount.takeIf { it != selectedPeopleCount }
    return copy(
        isShowBookmarkedPose = false,
        selectedPeopleCount = newPeopleCount,
        selectedRelationTag = selectedRelationTag.takeIf { newPeopleCount == PeopleCount.TWO },
    )
}

internal fun PoseState.toggleRelationTagFilter(relationTag: PoseRelationTag): PoseState =
    if (relationTag == selectedRelationTag) {
        copy(selectedRelationTag = null)
    } else {
        copy(
            isShowBookmarkedPose = false,
            selectedPeopleCount = PeopleCount.TWO,
            selectedRelationTag = relationTag,
        )
    }
