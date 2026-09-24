package com.neki.android.feature.map.impl.util

import android.location.Address
import android.location.Geocoder
import android.content.Context
import android.os.Build
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun Context.getSecondDepthRegionName(latitude: Double, longitude: Double): Result<String> =
    try {
        val regionName = withContext(Dispatchers.IO) {
            check(Geocoder.isPresent()) { "Geocoder를 사용할 수 없습니다." }
            val geocoder = Geocoder(this@getSecondDepthRegionName, Locale.KOREAN)
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (continuation.isActive) continuation.resume(addresses)
                        }

                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) {
                                continuation.resumeWithException(IOException(errorMessage ?: "지역명 조회 실패"))
                            }
                        }
                    })
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)
            }
            checkNotNull(extractSecondDepthRegionName(addresses?.firstOrNull()?.getAddressLine(0))) {
                "2단계 지역명을 찾을 수 없습니다."
            }
        }
        Result.success(regionName)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

/** 한국어 주소의 시·도 바로 다음 행정구역만 사용한다. 읍·면·동으로 대체하지 않는다. */
internal fun extractSecondDepthRegionName(address: String?): String? {
    val parts = address?.trim()?.split(Regex("\\s+"))
        ?.dropWhile { it == "대한민국" || it == "한국" }
        ?: return null
    val province = parts.firstOrNull() ?: return null
    val region = parts.getOrNull(1) ?: return null
    val provincePattern = Regex(
        "(?:[가-힣]+(?:시|도)|서울|부산|대구|인천|광주|대전|울산|세종|경기|강원|충북|충남|전북|전남|경북|경남|제주)",
    )
    return region.takeIf { province.matches(provincePattern) && it.matches(Regex("[가-힣]+[시군구]")) }
}
