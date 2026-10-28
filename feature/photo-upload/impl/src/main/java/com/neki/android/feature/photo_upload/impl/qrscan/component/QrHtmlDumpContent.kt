package com.neki.android.feature.photo_upload.impl.qrscan.component

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.neki.android.feature.photo_upload.impl.qrscan.util.QrHtmlCapture
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.TimeoutCancellationException
import timber.log.Timber

@Composable
internal fun QrHtmlDumpContent(
    sourceUrl: String,
    onCaptured: (String?) -> Unit,
) {
    val context = LocalContext.current
    val callback = rememberUpdatedState(onCaptured)
    val webView = remember(sourceUrl) {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.javaScriptCanOpenWindowsAutomatically = false
            isFocusable = false
            isClickable = false
            importantForAccessibility = WebView.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.webViewClient = WebViewClient()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = Modifier.size(1.dp).alpha(0f).clearAndSetSemantics {},
    )

    LaunchedEffect(webView) {
        val html = try {
            QrHtmlCapture.capture(webView, sourceUrl)
        } catch (_: TimeoutCancellationException) {
            Timber.tag("QrDump").w("HTML capture timed out")
            null
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Timber.tag("QrDump").w("HTML capture failed: %s", exception.javaClass.simpleName)
            null
        }
        currentCoroutineContext().ensureActive()
        callback.value(html)
    }
}
