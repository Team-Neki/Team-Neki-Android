package com.neki.android.core.data.remote.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: 추후 API 스펙 업데이트 시 제거
@Serializable
data class CreateFolderResponse(
    @SerialName("folderId") val folderId: Long,
)
