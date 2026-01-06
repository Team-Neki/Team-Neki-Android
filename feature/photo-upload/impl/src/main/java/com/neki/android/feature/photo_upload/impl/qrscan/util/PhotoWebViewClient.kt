package com.neki.android.feature.photo_upload.impl.qrscan.util

import android.webkit.WebViewClient

class PhotoWebViewClient(
    private val onImageUrlDetected: (String) -> Unit,
) : WebViewClient() {}
