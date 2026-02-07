package com.neki.android.feature.auth.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.HiltSharedViewModelStoreNavEntryDecorator
import com.neki.android.core.navigation.auth.AuthNavigator
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.feature.auth.api.AuthNavKey
import com.neki.android.feature.auth.api.navigateToLogin
import com.neki.android.feature.auth.api.navigateToOnboarding
import com.neki.android.feature.auth.api.navigateToTerm
import com.neki.android.feature.auth.impl.login.LoginRoute
import com.neki.android.feature.auth.impl.onboarding.OnboardingRoute
import com.neki.android.feature.auth.impl.splash.SplashRoute
import com.neki.android.feature.auth.impl.term.TermRoute

typealias AuthEntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

fun authEntryProvider(authNavigator: AuthNavigator): AuthEntryProviderInstaller = {
    authEntry(authNavigator)
}

private fun EntryProviderScope<NavKey>.authEntry(authNavigator: AuthNavigator) {
    entry<AuthNavKey.Splash>(
        clazzContentKey = { key -> key.toString() },
    ) {
        SplashRoute(
            navigateToOnboarding = authNavigator::navigateToOnboarding,
        )
    }

    entry<AuthNavKey.Onboarding>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            AuthNavKey.Splash.toString(),
        ),
    ) {
        OnboardingRoute(
            navigateToLogin = authNavigator::navigateToLogin,
        )
    }

    entry<AuthNavKey.Login>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            AuthNavKey.Splash.toString(),
        ),
    ) {
        LoginRoute(
            navigateToTerm = authNavigator::navigateToTerm,
        )
    }

    entry<AuthNavKey.Term>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            AuthNavKey.Splash.toString(),
        ),
    ) {
        TermRoute(
            navigateToMain = { authNavigator.navigateRoot(RootNavKey.Main) },
            navigateBack = authNavigator::goBack,
        )
    }
}
