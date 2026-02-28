package com.neki.android.feature.photo_upload.impl.qrscan.component

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import com.neki.android.feature.photo_upload.impl.qrscan.util.PhotoWebViewClient

@Composable
internal fun PhotoWebViewContent(
    scannedUrl: String,
    onDetectImageUrl: (String) -> Unit,
) {
    val webView = remember { mutableStateOf<WebView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            webView.value?.destroy()
        }
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webView.value = this
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                webViewClient = PhotoWebViewClient { photoImageUrl ->
                    onDetectImageUrl(photoImageUrl)
                }

                loadUrl(scannedUrl)
            }
        },
        update = { webView ->
            if (webView.url != scannedUrl && scannedUrl.isNotEmpty()) {
                webView.loadUrl(scannedUrl)
            }
        },
    )
}
