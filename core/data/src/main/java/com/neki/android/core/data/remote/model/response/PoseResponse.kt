package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.Pose
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoseResponse(
    @SerialName("hasNext") val hasNext: Boolean,
    @SerialName("items") val items: List<Item>,
) {
    @Serializable
    data class Item(
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
            peopleCount = PeopleCount.entries.find { it.name == headCount }?.value ?: 1,
            isScrapped = scrap,
        )
    }

    fun toModels() = items.map { it.toModel() }
}
