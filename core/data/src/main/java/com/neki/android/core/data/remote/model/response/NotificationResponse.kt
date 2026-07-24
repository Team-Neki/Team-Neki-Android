package com.neki.android.core.data.remote.model.response

import com.neki.android.core.common.util.toRelativeTime
import com.neki.android.core.model.Notification
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: Long,
    val type: String,
    val title: String,
    val body: String,
    val link: String?,
    val createdAt: String,
) {
    fun toModel() = Notification(
        id = id,
        type = type,
        title = title,
        body = body,
        link = link.orEmpty(),
        createdAt = createdAt.toRelativeTime(),
    )
}
