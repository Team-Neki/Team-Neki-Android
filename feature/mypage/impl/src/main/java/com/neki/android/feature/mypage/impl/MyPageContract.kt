package com.neki.android.feature.mypage.impl

data class MyPageState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val appVersion: String = "v1.3.1",
)

sealed interface MyPageIntent {
    data object ClickNotificationIcon : MyPageIntent
    data object ClickProfileCard : MyPageIntent
    data object ClickPermission : MyPageIntent
    data object ClickInquiry : MyPageIntent
    data object ClickTermsOfService : MyPageIntent
    data object ClickPrivacyPolicy : MyPageIntent
    data object ClickOpenSourceLicense : MyPageIntent
}

sealed interface MyPageEffect {
    data object NavigateToNotification : MyPageEffect
    data object NavigateToProfile : MyPageEffect
    data object NavigateToPermission : MyPageEffect
    data object NavigateToInquiry : MyPageEffect
    data object NavigateToTermsOfService : MyPageEffect
    data object NavigateToPrivacyPolicy : MyPageEffect
    data object NavigateToOpenSourceLicense : MyPageEffect
}
