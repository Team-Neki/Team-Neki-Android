package com.neki.android.core.navigation.root

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.auth.AuthNavigator
import com.neki.android.core.navigation.main.MainNavigator
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class RootNavigatorImpl @Inject constructor(
    private val rootState: RootNavigationState,
    private val authNavigator: AuthNavigator,
    private val mainNavigator: MainNavigator,
) : RootNavigator {

    override fun navigateToMain() {
        authNavigator.clear()
        rootState.stack.clear()
        rootState.stack.add(RootNavKey.Main)
    }

    override fun navigateToAuth(startKey: NavKey) {
        mainNavigator.clear()
        authNavigator.navigate(startKey)
        rootState.stack.clear()
        rootState.stack.add(RootNavKey.Auth(startKey))
    }
}
