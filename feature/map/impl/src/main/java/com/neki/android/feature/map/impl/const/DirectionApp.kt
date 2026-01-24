package com.neki.android.feature.map.impl.const

import androidx.annotation.DrawableRes
import com.neki.android.core.designsystem.R

enum class DirectionApp(
    @DrawableRes val appLogoRes: Int,
    val appName: String,
    val packageName: String,
) {
    GOOGLE_MAP(
        appLogoRes = R.drawable.image_google_map,
        appName = "구글맵",
        packageName = "com.google.android.apps.maps",
    ),
    NAVER_MAP(
        appLogoRes = R.drawable.image_naver_map,
        appName = "네이버 지도",
        packageName = "com.nhn.android.nmap",
    ),
    KAKAO_MAP(
        appLogoRes = R.drawable.image_kakao_map,
        appName = "카카오맵",
        packageName = "net.daum.android.map",
    ),
}
