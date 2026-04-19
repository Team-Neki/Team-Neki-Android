package com.neki.android.core.analytics.event

sealed interface MypageAnalyticsEvent : AnalyticsEvent {

    data object Logout : MypageAnalyticsEvent {
        override val name = "mypage_logout"
    }

    data object Withdraw : MypageAnalyticsEvent {
        override val name = "mypage_withdraw"
    }
}
