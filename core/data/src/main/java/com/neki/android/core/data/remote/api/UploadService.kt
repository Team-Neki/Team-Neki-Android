package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.MediaUploadTicketRequest
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.MediaUploadTicketResponse
import com.neki.android.core.data.remote.qualifier.UploadHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class UploadService @Inject constructor(
    @UploadHttpClient private val client: HttpClient,
) {
    // Media Upload Ticket 받기
    suspend fun getUploadTicket(requestBody: MediaUploadTicketRequest): BasicResponse<MediaUploadTicketResponse> {
        return client.post("/api/media/upload") { setBody(requestBody) }.body()
    }

    // PresignedUrl 에 파일 업로드
    suspend fun uploadImage(
        presignedUrl: String,
        imageBytes: ByteArray,
        contentType: String,
    ) {
        client.put(presignedUrl) {
            contentType(ContentType.parse(contentType))
            setBody(imageBytes)
        }
    }
}
