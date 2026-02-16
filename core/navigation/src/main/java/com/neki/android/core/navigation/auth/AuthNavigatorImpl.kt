package com.neki.android.core.navigation.auth

import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class AuthNavigatorImpl @Inject constructor(
    override val state: AuthNavigationState,
) : AuthNavigator {

    override fun navigate(key: NavKey) {
        state.stack.add(key)
    }

    override fun goBack() {
        state.stack.removeLastOrNull()
    }

    override fun remove(key: NavKey) {
        state.stack.remove(key)
    }

    override fun clear() {
        state.stack.clear()
    }
}
