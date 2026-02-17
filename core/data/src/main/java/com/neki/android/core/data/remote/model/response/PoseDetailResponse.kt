package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoseDetailResponse(
    @SerialName("poseId") val poseId: Long,
    @SerialName("headCount") val headCount: String,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("scrap") val bookmark: Boolean,
    @SerialName("contentType") val contentType: String,
    @SerialName("createdAt") val createdAt: String,
) {
    internal fun toModel() = Pose(
        id = poseId,
        isBookmarked = bookmark,
        poseImageUrl = imageUrl,
        peopleCount = PeopleCount.entries.find { it.name == headCount }?.value ?: 1,
    )
}
