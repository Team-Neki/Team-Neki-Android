package com.neki.android.feature.mypage.impl

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MyPageViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<MyPageState, MyPageIntent, MyPageEffect> =
        mviIntentStore(
            initialState = MyPageState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: MyPageIntent,
        state: MyPageState,
        reduce: (MyPageState.() -> MyPageState) -> Unit,
        postSideEffect: (MyPageEffect) -> Unit,
    ) {
        when (intent) {
            MyPageIntent.ClickNotificationIcon -> postSideEffect(MyPageEffect.NavigateToNotification)
            MyPageIntent.ClickProfileCard -> postSideEffect(MyPageEffect.NavigateToProfile)
            MyPageIntent.ClickPermission -> postSideEffect(MyPageEffect.NavigateToPermission)
            MyPageIntent.ClickInquiry -> postSideEffect(MyPageEffect.NavigateToInquiry)
            MyPageIntent.ClickTermsOfService -> postSideEffect(MyPageEffect.NavigateToTermsOfService)
            MyPageIntent.ClickPrivacyPolicy -> postSideEffect(MyPageEffect.NavigateToPrivacyPolicy)
            MyPageIntent.ClickOpenSourceLicense -> postSideEffect(MyPageEffect.NavigateToOpenSourceLicense)
        }
    }
}
