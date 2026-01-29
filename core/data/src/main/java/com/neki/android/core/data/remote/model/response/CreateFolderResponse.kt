package com.neki.android.core.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateFolderResponse(
    @SerialName("folderId") val folderId: Long,
)
