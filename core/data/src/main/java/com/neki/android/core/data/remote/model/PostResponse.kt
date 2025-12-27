package com.neki.android.core.data.remote.model

import com.neki.android.core.model.Post
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    @SerialName("userId") val userId: Int,
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String
) {
    fun toModel(): Post {
        return Post(
            userId = this.userId,
            id = this.id,
            title = this.title,
            body = this.body
        )
    }
}
