package com.neki.android.feature.map.impl.const

import androidx.annotation.DrawableRes
import com.neki.android.core.designsystem.R

enum class FourCutBrand(
    @DrawableRes val logoRes: Int,
    val brandName: String,
) {
    LIFE_FOUR_CUT(
        logoRes = R.drawable.icon_life_four_cut,
        brandName = "인생네컷",
    ),
    PHOTOGRAY(
        logoRes = R.drawable.icon_photogray,
        brandName = "포토그레이",
    ),
    PHOTOISM(
        logoRes = R.drawable.icon_photoism,
        brandName = "포토이즘",
    ),
    HARU_FILM(
        logoRes = R.drawable.icon_haru_film,
        brandName = "하루필름",
    ),
    PLANB_STUDIO(
        logoRes = R.drawable.icon_planb_studio,
        brandName = "플랜비\n스튜디오",
    ),
    PHOTO_SIGNATURE(
        logoRes = R.drawable.icon_photo_signature,
        brandName = "포토시그니처",
    ),
}
