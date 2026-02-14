package com.neki.android.core.model

data class MediaUploadTicket(
    val mediaId: Long,
    val uploadUrl: String,
)

class MediaMetaData(
    val width: Int,
    val height: Int,
    val imageBytes: ByteArray,
)

enum class MediaType(val label: String) {
    USER_PROFILE("user-profiles"),
    PHOTO_BOOTH("photo-booth"),
    ATTACHMENT("attachments"),
}

enum class ContentType(val label: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
    ;

    companion object {
        fun fromString(type: String) = when {
            type.contains("jpg", ignoreCase = true) -> JPEG
            type.contains("jpeg", ignoreCase = true) -> JPEG
            type.contains("png", ignoreCase = true) -> PNG
            else -> JPEG
        }
    }
}
