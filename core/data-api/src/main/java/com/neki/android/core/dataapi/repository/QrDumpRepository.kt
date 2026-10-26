package com.neki.android.core.dataapi.repository

interface QrDumpRepository {
    /** Uploads UTF-8 HTML with the original QR URL recorded in a source comment. */
    suspend fun uploadHtml(sourceUrl: String, html: String): Result<Unit>
}
