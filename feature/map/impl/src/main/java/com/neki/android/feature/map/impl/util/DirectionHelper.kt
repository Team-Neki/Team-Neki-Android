package com.neki.android.feature.map.impl.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import com.neki.android.feature.map.impl.const.DirectionApp

internal object DirectionHelper {
    suspend fun navigateToUrl(
        context: Context,
        app: DirectionApp,
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double,
    ) {
        val url = when (app) {
            DirectionApp.GOOGLE_MAP -> "google.navigation:q=$endLatitude,$endLongitude&mode=w"
            DirectionApp.NAVER_MAP -> {
                val startName = context.getPlaceName(startLatitude, startLongitude, "출발지")
                val destName = context.getPlaceName(endLatitude, endLongitude, "도착지")
                "nmap://route/walk?slat=$startLatitude&slng=$startLongitude&sname=$startName&dlat=$endLatitude&dlng=$endLongitude&dname=$destName"
            }
            DirectionApp.KAKAO_MAP -> "kakaomap://route?sp=$startLatitude,$startLongitude&ep=$endLatitude,$endLongitude&by=FOOT"
        }
        moveAppOrStore(context, url, app.packageName)
    }

    private fun moveAppOrStore(
        context: Context,
        url: String,
        packageName: String,
    ) {
        val intent = if (isInstalled(context.packageManager, packageName)) {
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                setPackage(packageName)
            }
        } else {
            Intent(
                Intent.ACTION_VIEW,
                "market://details?id=$packageName".toUri(),
            )
        }
        context.startActivity(intent)
    }

    private fun isInstalled(
        packageManager: PackageManager,
        packageName: String,
    ): Boolean = runCatching { packageManager.getPackageInfo(packageName, 0) }.isSuccess
}
