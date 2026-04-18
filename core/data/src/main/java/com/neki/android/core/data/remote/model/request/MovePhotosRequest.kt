package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovePhotosRequest(
    @SerialName("sourceFolderId") val sourceFolderId: Long,
    @SerialName("photoIds") val photoIds: List<Long>,
    @SerialName("targetFolderIds") val targetFolderIds: List<Long>,
)
