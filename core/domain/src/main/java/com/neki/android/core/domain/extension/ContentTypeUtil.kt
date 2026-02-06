package com.neki.android.core.domain.extension

import com.neki.android.core.model.ContentType
import java.util.UUID

object ContentTypeUtil {
    fun generateFileName(contentType: ContentType): String {
        val extension = when (contentType) {
            ContentType.JPEG -> "jpeg"
            ContentType.PNG -> "png"
        }
        return "${UUID.randomUUID()}.$extension"
    }
}
