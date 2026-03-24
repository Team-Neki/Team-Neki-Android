package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.qualifier.WebhookHttpClient
import com.neki.android.core.dataapi.repository.DiscordWebhookRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiscordWebhookRepositoryImpl @Inject constructor(
    @WebhookHttpClient private val client: HttpClient,
) : DiscordWebhookRepository {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun logUnsupportedBrandQR(scannedUrl: String) {
        val message = "**[미지원 브랜드 QR]**\\nURL: $scannedUrl\\n시간: ${LocalDateTime.now().format(formatter)}"
        send(message)
    }

    override suspend fun logWebViewExitWithoutImage(scannedUrl: String) {
        val message = "**[이미지 미감지 이탈]**\\nURL: $scannedUrl\\n시간: ${LocalDateTime.now().format(formatter)}"
        send(message)
    }

    private suspend fun send(content: String) {
        runCatching {
            client.post("") {
                setBody("""{"content":"$content"}""")
            }
        }.onFailure { e ->
            Timber.e(e, "Discord webhook 전송 실패")
        }
    }
}
