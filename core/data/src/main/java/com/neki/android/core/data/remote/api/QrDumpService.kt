package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.QrDumpUploadTicketResponse
import com.neki.android.core.data.remote.qualifier.UploadHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import javax.inject.Inject
import timber.log.Timber

class QrDumpService @Inject constructor(
    private val client: HttpClient,
    @UploadHttpClient private val uploadClient: HttpClient,
) {
    suspend fun getUploadTicket(): BasicResponse<QrDumpUploadTicketResponse> =
        client.post(UPLOAD_TICKET_PATH).body()

    suspend fun uploadHtml(ticket: QrDumpUploadTicketResponse, htmlBytes: ByteArray) {
        val response = uploadClient.put(ticket.uploadTicket) {
            header(HttpHeaders.ContentType, ticket.contentType)
            setBody(htmlBytes)
        }
        Timber.tag("QrDump").d("S3 upload completed: status=%d", response.status.value)
    }

    companion object {
        const val UPLOAD_TICKET_PATH = "/api/media/qr-dumps/upload"
    }
}
