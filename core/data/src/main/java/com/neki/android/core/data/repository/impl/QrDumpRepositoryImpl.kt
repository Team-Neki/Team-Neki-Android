package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.QrDumpService
import com.neki.android.core.data.remote.model.response.QrDumpUploadTicketResponse
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.QrDumpRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.URI
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class QrDumpRepositoryImpl @Inject constructor(
    private val service: QrDumpService,
) : QrDumpRepository {
    override suspend fun uploadHtml(sourceUrl: String, html: String): Result<Unit> = runSuspendCatching {
        val bytes = withContext(Dispatchers.Default) { createPayload(sourceUrl, html) }
        repeat(2) { attempt ->
            var ticket: QrDumpUploadTicketResponse? = null
            try {
                ticket = getValidTicket()
                service.uploadHtml(ticket, bytes)
                return@runSuspendCatching
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (attempt == 1 || !isRetryable(exception, ticket)) throw exception
                delay(500L)
            }
        }
    }

    private fun isRetryable(exception: Exception, ticket: QrDumpUploadTicketResponse?): Boolean = when (exception) {
        is ClientRequestException -> exception.response.status == HttpStatusCode.RequestTimeout ||
            (exception.response.status == HttpStatusCode.Forbidden && ticket?.isExpired() == true)
        is ServerResponseException, is HttpRequestTimeoutException, is IOException -> true
        else -> false
    }

    private suspend fun getValidTicket(): QrDumpUploadTicketResponse {
        repeat(2) {
            val response = service.getUploadTicket()
            check(response.resultCode == "D-0") { "QR dump ticket request failed" }
            val ticket = response.data
            require(ticket.method == "PUT") { "Unsupported QR dump upload method" }
            val uploadUri = URI(ticket.uploadTicket)
            require(uploadUri.scheme == "https" && !uploadUri.host.isNullOrBlank()) { "Invalid QR dump upload URL" }
            require(ticket.contentType.isNotBlank()) { "Missing QR dump content type" }
            if (!ticket.isExpired()) return ticket
        }
        error("QR dump upload ticket expired")
    }

    private fun QrDumpUploadTicketResponse.isExpired(): Boolean {
        val expiresAt = try {
            Instant.parse(expiresIn)
        } catch (_: DateTimeParseException) {
            // Server InstantCustomSerializer emits UTC without an offset; never use the device timezone.
            LocalDateTime.parse(expiresIn).toInstant(ZoneOffset.UTC)
        }
        return !expiresAt.isAfter(Instant.now())
    }

    private fun createPayload(sourceUrl: String, html: String): ByteArray {
        val sourceUri = URI(sourceUrl)
        require(sourceUri.scheme?.lowercase() in listOf("http", "https") && !sourceUri.host.isNullOrBlank()) { "Invalid QR dump source URL" }
        require(html.isNotBlank()) { "QR dump HTML is empty" }
        // Escape comment delimiters without changing the HTML document itself.
        val commentUrl = sourceUrl.replace("--", "&#45;&#45;")
        val bytes = "<!-- source: $commentUrl -->\n$html".toByteArray(Charsets.UTF_8)
        return bytes
    }
}
