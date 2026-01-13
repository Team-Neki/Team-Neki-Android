package com.neki.android.feature.auth.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface LoginNavKey : NavKey {

    @Serializable
    data object Login : LoginNavKey
}

fun Navigator.navigateToLogin() {
    navigate(LoginNavKey.Login)
}
