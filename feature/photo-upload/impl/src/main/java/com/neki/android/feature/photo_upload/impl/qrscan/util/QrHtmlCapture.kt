package com.neki.android.feature.photo_upload.impl.qrscan.util

import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URI
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONTokener
import timber.log.Timber

internal object QrHtmlCapture {
    private const val CAPTURE_TIMEOUT_MS = 15_000L
    private const val SAMPLE_INTERVAL_MS = 500L
    private const val MIN_SAMPLES = 5
    private const val MAX_SAMPLES = 12

    fun isHttpUrl(url: String): Boolean = runCatching {
        val uri = URI(url)
        uri.scheme?.lowercase() in listOf("http", "https") && !uri.host.isNullOrBlank()
    }.getOrDefault(false)

    /** Must run on the main thread. Captures the rendered top-level DOM, without a JS interface. */
    suspend fun capture(webView: WebView, sourceUrl: String): String = withTimeout(CAPTURE_TIMEOUT_MS) {
        require(isHttpUrl(sourceUrl)) { "Invalid QR HTML source URL" }
        var pageFinished = false
        var navigation = 0
        var failed = false
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                pageFinished = false
                navigation++
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (url == view?.url && url != "about:blank") pageFinished = true
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
                request == null || !isHttpUrl(request.url.toString())

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    failed = true
                    Timber.tag("QrDump").w("HTML main frame load error: code=%d", error?.errorCode)
                }
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                if (request?.isForMainFrame == true) {
                    failed = true
                    Timber.tag("QrDump").w("HTML main frame HTTP error: status=%d", errorResponse?.statusCode)
                }
            }

            override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                failed = true
                return true
            }
        }
        webView.loadUrl(sourceUrl)
        var lastVersion: Long? = null
        var lastNavigation = -1
        var stableSamples = 0
        var samples = 0
        while (samples < MAX_SAMPLES) {
            delay(SAMPLE_INTERVAL_MS)
            check(!failed) { "QR HTML main frame failed to load" }
            if (!pageFinished) continue
            val currentNavigation = navigation
            val version = webView.readDomVersion() ?: continue
            if (!pageFinished || currentNavigation != navigation) continue
            if (lastNavigation != currentNavigation) {
                samples = 0
                stableSamples = 0
                lastVersion = null
            }
            stableSamples = if (version == lastVersion) stableSamples + 1 else 0
            lastVersion = version
            lastNavigation = currentNavigation
            samples++
            if (samples >= MAX_SAMPLES || (samples >= MIN_SAMPLES && stableSamples >= 2)) {
                val html = webView.readHtml()
                check(!failed) { "QR HTML main frame failed to load" }
                if (pageFinished && currentNavigation == navigation && html != null) return@withTimeout html
                samples = 0
            }
        }
        error("QR HTML capture did not complete")
    }

    // Poll only a small mutation counter, transferring the full DOM once at capture time.
    private suspend fun WebView.readDomVersion(): Long? = evaluateScript(
        """
            (function() {
                if (document.readyState !== 'complete' || !document.documentElement) return null;
                if (!window.__nekiQrDumpObserver) {
                    var state = { version: 0 };
                    state.observer = new MutationObserver(function() { state.version++; });
                    state.observer.observe(document.documentElement, {
                        subtree: true, childList: true, attributes: true, characterData: true
                    });
                    window.__nekiQrDumpObserver = state;
                }
                return window.__nekiQrDumpObserver.version;
            })()
        """.trimIndent(),
    ).toLongOrNull()

    private suspend fun WebView.readHtml(): String? {
        val result = evaluateScript(
            """
                (function() {
                    if (document.readyState !== 'complete' || !document.documentElement) return null;
                    return document.documentElement.outerHTML;
                })()
            """.trimIndent(),
        )
        return withContext(Dispatchers.Default) {
            (JSONTokener(result).nextValue() as? String)?.takeIf { it.isNotBlank() }
        }
    }

    private suspend fun WebView.evaluateScript(script: String): String = suspendCancellableCoroutine { continuation ->
        evaluateJavascript(script) { result ->
            if (continuation.isActive) continuation.resume(result)
        }
    }
}
