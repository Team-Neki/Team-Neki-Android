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
            type.contains("jpg") -> JPEG
            type.contains("jpeg") -> JPEG
            type.contains("png") -> PNG
            else -> JPEG
        }
    }
}
