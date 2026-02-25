package com.neki.android.feature.photo_upload.impl.qrscan.util

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.neki.android.feature.photo_upload.impl.BuildConfig
import timber.log.Timber

class PhotoWebViewClient(
    private val onImageUrlDetected: (String) -> Unit,
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?,
    ): WebResourceResponse? {
        val url = request?.url.toString()
        Timber.d(request?.url.toString())

        when {
            // 포토이즘
            url.contains(BuildConfig.PHOTOISM_IMAGE_URL) && url.endsWith(BuildConfig.PHOTOISM_IMAGE_URL_MIME_TYPE) -> {
                Timber.d("포토이즘 이미지")
                onImageUrlDetected(url)
            }

            // 인생네컷
            url.contains(BuildConfig.LIFE_FOUR_CUT_IMAGE_URL) && url.endsWith(BuildConfig.LIFE_FOUR_CUT_IMAGE_URL_MIME_TYPE) -> {
                Timber.d("인생네컷 이미지")
                onImageUrlDetected(url)
            }

            // 포토시그니처
            url.contains(BuildConfig.PHOTO_SIGNATURE_IMAGE_URL) && url.endsWith(BuildConfig.PHOTO_SIGNATURE_IMAGE_URL_MIME_TYPE) -> {
                Timber.d("포토시그니처 이미지")
                onImageUrlDetected(url)
            }

            // 하루필름
            url.contains(BuildConfig.HARU_FILM_IMAGE_URL) && url.endsWith(BuildConfig.HARU_FILM_IMAGE_URL_MIME_TYPE) -> {
                Timber.d("하루필름 이미지")
                onImageUrlDetected(url)
            }

            // 포토그레이
            url.contains(BuildConfig.PHOTO_GRAY_IMAGE_URL) && url.endsWith(BuildConfig.PHOTO_GRAY_IMAGE_URL_MIME_TYPE) -> {
                Timber.d("포토그레이 이미지")
                onImageUrlDetected(url)
            }
        }

        return super.shouldInterceptRequest(view, request)
    }
}
