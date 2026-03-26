package com.neki.android.core.dataapi.repository

interface DiscordWebhookRepository {
    suspend fun logUnsupportedBrandQR(scannedUrl: String)
    suspend fun logWebViewExitWithoutImage(scannedUrl: String)
}
