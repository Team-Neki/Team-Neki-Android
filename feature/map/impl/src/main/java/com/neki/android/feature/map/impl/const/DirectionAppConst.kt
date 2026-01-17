package com.neki.android.feature.map.impl.const

import androidx.annotation.DrawableRes
import com.neki.android.core.designsystem.R

enum class DirectionAppConst(
    @DrawableRes val appLogoRes: Int,
    val appName: String,
) {
    GOOGLE_MAP(
        appLogoRes = R.drawable.icon_google_map,
        appName = "구글맵",
    ),
    NAVER_MAP(
        appLogoRes = R.drawable.icon_naver_map,
        appName = "네이버 지도",
    ),
    KAKAO_MAP(
        appLogoRes = R.drawable.icon_kakao_map,
        appName = "카카오맵",
    ),
}
