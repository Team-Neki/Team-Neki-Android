package com.neki.android.feature.map.impl.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.naver.maps.geometry.LatLng
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.cluster.PhotoBoothClusterItem
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** 위경도에 대한 지명 조회 **/
internal suspend fun Context.getPlaceName(
    latitude: Double,
    longitude: Double,
    fallback: String,
): String = suspendCancellableCoroutine { coroutine ->
    val geocoder = Geocoder(this, Locale.KOREAN)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
            val address = addresses.firstOrNull()
                ?.getAddressLine(0)
                ?.removePrefix("대한민국 ")
                ?: fallback
            coroutine.resume(address)
        }
    } else {
        try {
            @Suppress("DEPRECATION")
            val address = geocoder.getFromLocation(latitude, longitude, 1)
                ?.firstOrNull()
                ?.getAddressLine(0)
                ?.removePrefix("대한민국 ")
                ?: fallback
            coroutine.resume(address)
        } catch (e: Exception) {
            e.printStackTrace()
            coroutine.resume(fallback)
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

/** 같은 좌표에 겹치는 마커를 삼각함수로 분산 배치 **/
internal fun List<PhotoBooth>.toJitteredClusterItems(): Map<Long, PhotoBoothClusterItem> {
    val offsetRadius = 0.00005
    val coordinateFrequency = mutableMapOf<Pair<Double, Double>, Int>()
    return associate { booth ->
        val key = booth.latitude to booth.longitude
        val overlapIndex = coordinateFrequency.getOrDefault(key, 0)
        coordinateFrequency[key] = overlapIndex + 1
        val position = if (overlapIndex == 0) {
            null
        } else {
            val angle = overlapIndex * (PI / 3)
            LatLng(
                booth.latitude + offsetRadius * cos(angle),
                booth.longitude + offsetRadius * sin(angle),
            )
        }
        booth.id to PhotoBoothClusterItem(booth, position)
    }
}
