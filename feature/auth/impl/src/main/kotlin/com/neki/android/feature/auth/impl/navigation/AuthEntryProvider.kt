package com.neki.android.feature.auth.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.auth.AuthNavigator
import com.neki.android.core.navigation.root.RootNavigator
import com.neki.android.feature.auth.api.AuthNavKey
import com.neki.android.feature.auth.api.navigateToLogin
import com.neki.android.feature.auth.api.navigateToOnboarding
import com.neki.android.feature.auth.api.navigateToTerm
import com.neki.android.feature.auth.impl.login.LoginRoute
import com.neki.android.feature.auth.impl.onboarding.OnboardingRoute
import com.neki.android.feature.auth.impl.splash.SplashRoute
import com.neki.android.feature.auth.impl.term.TermRoute

typealias AuthEntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

fun authEntryProvider(
    rootNavigator: RootNavigator,
    authNavigator: AuthNavigator,
): AuthEntryProviderInstaller = {
    authEntry(rootNavigator, authNavigator)
}

private fun EntryProviderScope<NavKey>.authEntry(
    rootNavigator: RootNavigator,
    authNavigator: AuthNavigator,
) {
    entry<AuthNavKey.Splash> { key ->
        SplashRoute(
            navigateToOnboarding = {
                authNavigator.remove(key)
                authNavigator.navigateToOnboarding()
            },
            navigateToLogin = {
                authNavigator.remove(key)
                authNavigator.navigateToLogin()
            },
            navigateToMain = rootNavigator::navigateToMain,
        )
    }

    entry<AuthNavKey.Onboarding> { key ->
        OnboardingRoute(
            navigateToLogin = {
                authNavigator.remove(key)
                authNavigator.navigateToLogin()
            },
        )
    }

    entry<AuthNavKey.Login> {
        LoginRoute(
            navigateToTerm = authNavigator::navigateToTerm,
            navigateToMain = rootNavigator::navigateToMain,
        )
    }

    entry<AuthNavKey.Term> {
        TermRoute(
            navigateToMain = rootNavigator::navigateToMain,
            navigateBack = authNavigator::goBack,
        )
    }
}
