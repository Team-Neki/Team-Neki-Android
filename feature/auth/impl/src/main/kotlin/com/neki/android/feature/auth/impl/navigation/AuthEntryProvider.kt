package com.neki.android.feature.auth.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.auth.AuthNavigator
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.feature.auth.api.AuthNavKey
import com.neki.android.feature.auth.api.navigateToLogin
import com.neki.android.feature.auth.api.navigateToOnboarding
import com.neki.android.feature.auth.impl.LoginRoute
import com.neki.android.feature.auth.impl.onboarding.OnboardingRoute
import com.neki.android.feature.auth.impl.splash.SplashRoute

typealias AuthEntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

fun authEntryProvider(authNavigator: AuthNavigator): AuthEntryProviderInstaller = {
    authEntry(authNavigator)
}

private fun EntryProviderScope<NavKey>.authEntry(authNavigator: AuthNavigator) {
    entry<AuthNavKey.Splash> {
        SplashRoute(
            navigateToOnboarding = authNavigator::navigateToOnboarding,
        )
    }

    entry<AuthNavKey.Onboarding> {
        OnboardingRoute(
            navigateToLogin = authNavigator::navigateToLogin,
        )
    }

    entry<AuthNavKey.Login> {
        LoginRoute(
            navigateToMain = { authNavigator.navigateRoot(RootNavKey.Main) },
        )
    }
}
