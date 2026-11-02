package com.neki.android.core.data.analytics

import kotlinx.serialization.json.Json

/** Shared by the wire client and UTF-8 batch size calculation. */
internal val clientLogJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}
