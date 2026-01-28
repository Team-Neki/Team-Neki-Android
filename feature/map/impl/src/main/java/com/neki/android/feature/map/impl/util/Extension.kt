package com.neki.android.feature.map.impl.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

/** 위경도에 대한 지명 조회 **/
internal suspend fun Context.getPlaceName(
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
            e.printStackTrace()
            cont.resume(fallback)
        }
    }
}

/** 두 위경도 좌표 사이 거리 계산 (Haversine formula) **/
internal fun calculateDistance(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double,
): Int {
    val earthRadius = 6371000.0 // meters
    val dLat = Math.toRadians(endLatitude - startLatitude)
    val dLng = Math.toRadians(endLongitude - startLongitude)
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
        kotlin.math.cos(Math.toRadians(startLatitude)) * kotlin.math.cos(Math.toRadians(endLatitude)) *
        kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return (earthRadius * c).toInt()
}

/** 거리(m)를 단위 포함 문자열로 변환 **/
internal fun Int.formatDistance(): String {
    return if (this < 1000) {
        "${this}m"
    } else {
        val km = this / 1000.0
        val roundedKm = kotlin.math.round(km * 10) / 10.0
        if (roundedKm == roundedKm.toLong().toDouble()) {
            "${roundedKm.toLong()}km"
        } else {
            "${roundedKm}km"
        }
    }
}
