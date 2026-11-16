package com.neki.android.feature.pose.impl.main

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.PoseRelation

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

internal fun PoseState.toggleRelationTagFilter(relationTag: PoseRelation): PoseState =
    if (relationTag == selectedRelationTag) {
        copy(selectedRelationTag = null)
    } else {
        copy(
            isShowBookmarkedPose = false,
            selectedPeopleCount = PeopleCount.TWO,
            selectedRelationTag = relationTag,
        )
    }

internal data class PoseListQuery(
    val headCount: PeopleCount? = null,
    val relation: PoseRelation? = null,
    val isBookmarkOnly: Boolean = false,
)

internal fun PoseState.toPoseListQuery(): PoseListQuery = PoseListQuery(
    headCount = selectedPeopleCount,
    relation = selectedRelationTag,
    isBookmarkOnly = isShowBookmarkedPose,
)

internal fun PeopleCount?.isRelationTagVisible(): Boolean = this == null || this == PeopleCount.TWO
