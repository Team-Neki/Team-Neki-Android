package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.qualifier.WebhookHttpClient
import com.neki.android.core.dataapi.repository.DiscordWebhookRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiscordWebhookRepositoryImpl @Inject constructor(
    @WebhookHttpClient private val client: HttpClient,
) : DiscordWebhookRepository {

    override suspend fun logUnsupportedBrandQR(scannedUrl: String) {
        sendEmbed(
            description = "지원하지 않는 브랜드의 QR 코드가 스캔되었습니다.",
            color = 15548997,
            scannedUrl = scannedUrl,
        )
    }

    override suspend fun logWebViewExitWithoutImage(scannedUrl: String) {
        sendEmbed(
            description = "지원 브랜드 QR로 WebView에 진입했으나, 이미지 URL을 감지하지 못한 채 이탈했습니다.",
            color = 16776960,
            scannedUrl = scannedUrl,
        )
    }

    private suspend fun sendEmbed(description: String, color: Int, scannedUrl: String) {
        val timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val payload = """
            {
                "embeds": [{
                    "title": "\uD83D\uDEA8 미지원 QR 코드 스캔 감지  [Android]",
                    "description": "$description",
                    "color": $color,
                    "fields": [
                        {"name": "URL", "value": "$scannedUrl", "inline": false}
                    ],
                    "timestamp": "$timestamp"
                }]
            }
        """.trimIndent()

        runCatching {
            client.post("") {
                setBody(payload)
            }
        }.onFailure { e ->
            Timber.e(e, "Discord webhook 전송 실패")
        }
    }
}
