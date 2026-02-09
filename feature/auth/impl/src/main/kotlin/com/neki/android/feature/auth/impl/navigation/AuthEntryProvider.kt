package com.neki.android.feature.auth.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.auth.AuthNavigator
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.feature.auth.api.AuthNavKey
import com.neki.android.feature.auth.api.navigateToLoginAndClear
import com.neki.android.feature.auth.api.navigateToOnboardingAndClear
import com.neki.android.feature.auth.api.navigateToTerm
import com.neki.android.feature.auth.impl.login.LoginRoute
import com.neki.android.feature.auth.impl.onboarding.OnboardingRoute
import com.neki.android.feature.auth.impl.splash.SplashRoute
import com.neki.android.feature.auth.impl.term.TermRoute

typealias AuthEntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

fun authEntryProvider(authNavigator: AuthNavigator): AuthEntryProviderInstaller = {
    authEntry(authNavigator)
}

private fun EntryProviderScope<NavKey>.authEntry(navigator: AuthNavigator) {
    entry<AuthNavKey.Splash> {
        SplashRoute(
            navigateToOnboarding = navigator::navigateToOnboardingAndClear,
            navigateToLogin = navigator::navigateToLoginAndClear,
            navigateToMain = { navigator.navigateRoot(RootNavKey.Main) },
        )
    }

    entry<AuthNavKey.Onboarding> {
        OnboardingRoute(
            navigateToLogin = navigator::navigateToLoginAndClear,
        )
    }

    entry<AuthNavKey.Login> {
        LoginRoute(
            navigateToTerm = navigator::navigateToTerm,
        )
    }

    entry<AuthNavKey.Term> {
        TermRoute(
            navigateToMain = { navigator.navigateRoot(RootNavKey.Main) },
            navigateBack = navigator::goBack,
        )
    }
}
