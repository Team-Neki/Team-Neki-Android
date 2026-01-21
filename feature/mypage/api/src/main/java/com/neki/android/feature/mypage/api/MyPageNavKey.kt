package com.neki.android.feature.mypage.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface MyPageNavKey : NavKey {

    @Serializable
    data object MyPage : MyPageNavKey

    @Serializable
    data object Permission : MyPageNavKey
}

fun Navigator.navigateToMyPage() {
    navigate(MyPageNavKey.MyPage)
}

fun Navigator.navigateToPermission() {
    navigate(MyPageNavKey.Permission)
}
