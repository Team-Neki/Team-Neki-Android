package com.neki.android.feature.map.impl.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

suspend fun Context.getPlaceName(
    latitude: Double,
    longitude: Double,
    fallback: String,
): String = suspendCancellableCoroutine { cont ->
    val geocoder = Geocoder(this, Locale.KOREAN)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
            val address = addresses.firstOrNull()
                ?.getAddressLine(0)
                ?.removePrefix("대한민국 ")
                ?: fallback
            cont.resume(address)
        }
    } else {
        try {
            @Suppress("DEPRECATION")
            val address = geocoder.getFromLocation(latitude, longitude, 1)
                ?.firstOrNull()
                ?.getAddressLine(0)
                ?.removePrefix("대한민국 ")
                ?: fallback
            cont.resume(address)
        } catch (e: Exception) {
            cont.resume(fallback)
        }
    }
}
