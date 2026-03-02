package com.neki.android.feature.photo_upload.impl.qrscan.component

import android.provider.SyncStateContract.Helpers.update
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.feature.photo_upload.impl.qrscan.util.PhotoWebViewClient

@Composable
internal fun PhotoWebViewContent(
    scannedUrl: String,
    onDetectImageUrl: (String) -> Unit,
) {
    val webView = remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }

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

                webViewClient = PhotoWebViewClient(
                    onPageFinished = { isLoading = false },
                ) { photoImageUrl ->
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
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    )

    if (isLoading) {
        LoadingDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
            onDismissRequest = { isLoading = false },
        )
    }
}
