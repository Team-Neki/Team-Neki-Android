package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoseResponse(
    @SerialName("hasNext") val hasNext: Boolean,
    @SerialName("items") val items: List<PoseItemResponse>,
) {
    fun toModels() = items.map { it.toModel() }
}

@Serializable
data class PoseItemResponse(
    @SerialName("poseId") val poseId: Long,
    @SerialName("headCount") val headCount: String,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("scrap") val scrap: Boolean,
    @SerialName("contentType") val contentType: String,
    @SerialName("createdAt") val createdAt: String,
) {
    internal fun toModel() = Pose(
        id = poseId,
        poseImageUrl = imageUrl,
        isScrapped = scrap,
        peopleCount = PeopleCount.entries.find { it.name == headCount }?.value ?: 1,
    )
}
