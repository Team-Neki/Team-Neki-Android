package com.neki.android.feature.auth.impl.onboarding.model

import androidx.annotation.DrawableRes
import com.neki.android.core.designsystem.R

enum class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String,
) {
    BOOTH_SEARCH(
        imageRes = R.drawable.image_onboarding_01,
        title = "빠른 네컷 부스 탐색",
        description = "네컷 부스 정보를\n빠르게 쉽게 찾아요",
    ),
    POSE_RECOMMEND(
        imageRes = R.drawable.image_onboarding_02,
        title = "포즈 걱정 없는 촬영 경험",
        description = "인원수에 맞는\n포즈를 추천받아요",
    ),
    PHOTO_ARCHIVE(
        imageRes = R.drawable.image_onboarding_03,
        title = "네컷 사진 아카이빙",
        description = "흩어지기 쉬운 사진을\n한곳에 모아요",
    ),
}
