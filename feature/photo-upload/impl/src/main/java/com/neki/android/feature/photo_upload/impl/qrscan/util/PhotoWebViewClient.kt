package com.neki.android.feature.photo_upload.impl.qrscan.util

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.neki.android.feature.photo_upload.impl.BuildConfig

class PhotoWebViewClient(
    private val onImageUrlDetected: (String) -> Unit,
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?,
    ): WebResourceResponse? {
        val url = request?.url.toString()

        // Photoism
        if (url.contains(BuildConfig.PHOTOISM_URL) && url.endsWith(BuildConfig.PHOTOISM_IMG_URL_END)) {
            onImageUrlDetected(url)
        }

        return super.shouldInterceptRequest(view, request)
    }
}
