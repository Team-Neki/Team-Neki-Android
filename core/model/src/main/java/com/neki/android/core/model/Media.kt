package com.neki.android.core.model

data class Media(
    val mediaId: Long = 0L,
    val folderId: Long? = null,
    val fileName: String = "",
    val contentType: ContentType = ContentType.JPEG,
    val mediaType: MediaType = MediaType.PHOTO_BOOTH,
)

enum class MediaType(val label: String) {
    USER_PROFILE("user-profiles"),
    PHOTO_BOOTH("photo-booth"),
    ATTACHMENT("attachments"),
}

enum class ContentType(val label: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
}
