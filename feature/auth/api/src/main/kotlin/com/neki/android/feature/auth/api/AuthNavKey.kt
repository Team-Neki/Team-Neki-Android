package com.neki.android.feature.auth.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.auth.AuthNavigator
import kotlinx.serialization.Serializable

sealed interface AuthNavKey : NavKey {

    @Serializable
    data object Splash : AuthNavKey

    @Serializable
    data object Onboarding : AuthNavKey

    @Serializable
    data object Login : AuthNavKey

    @Serializable
    data object Term : AuthNavKey
}

fun AuthNavigator.navigateToOnboarding() {
    navigate(AuthNavKey.Onboarding)
}

fun AuthNavigator.navigateToLogin() {
    navigate(AuthNavKey.Login)
}

fun AuthNavigator.navigateToTerm() {
    navigate(AuthNavKey.Term)
}
