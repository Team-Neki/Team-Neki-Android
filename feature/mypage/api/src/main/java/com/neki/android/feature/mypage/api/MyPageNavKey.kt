package com.neki.android.feature.mypage.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface MyPageNavKey : NavKey {

    @Serializable
    data object MyPage : MyPageNavKey

    @Serializable
    data object Permission : MyPageNavKey

    @Serializable
    data object Profile : MyPageNavKey

    @Serializable
    data object EditProfile : MyPageNavKey
}

fun MainNavigator.navigateToMyPage() {
    navigate(MyPageNavKey.MyPage)
}

fun MainNavigator.navigateToPermission() {
    navigate(MyPageNavKey.Permission)
}

fun MainNavigator.navigateToProfile() {
    navigate(MyPageNavKey.Profile)
}

fun MainNavigator.navigateToEditProfile() {
    navigate(MyPageNavKey.EditProfile)
}
