package com.neki.android.core.common.extension

import android.net.Uri

fun Uri?.toBase64(): String {
    return this.toString()
}
