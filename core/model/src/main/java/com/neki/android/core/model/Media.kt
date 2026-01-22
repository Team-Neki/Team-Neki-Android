package com.neki.android.core.model

enum class MediaType(val label: String) {
    USER_PROFILE("user-profiles"),
    PHOTO_BOOTH("photo-booth"),
    ATTACHMENT("attachments"),
}

enum class ContentType(val label: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
}
