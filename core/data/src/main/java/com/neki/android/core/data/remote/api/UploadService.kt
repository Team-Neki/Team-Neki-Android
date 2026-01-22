package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.qualifier.UploadHttpClient
import com.neki.android.core.model.ContentType
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File
import javax.inject.Inject

class UploadService @Inject constructor(
    @UploadHttpClient private val client: HttpClient,
) {
    // PresignedUrl 에 파일 업로드
    suspend fun uploadImage(fileName: String, presignedUrl: String, imageFile: File, contentType: ContentType) {
        client.submitFormWithBinaryData(
            url = presignedUrl,
            formData = formData {
                append(
                    "image",
                    imageFile.readBytes(),
                    Headers.build {
                        append(HttpHeaders.ContentType, contentType.label)
                        append(HttpHeaders.ContentDisposition, "filename=$fileName")
                    },
                )
            },
        )
    }
}
