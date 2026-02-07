package com.neki.android.core.navigation.auth

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.core.navigation.root.RootNavigationState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class AuthNavigatorImpl @Inject constructor(
    private val rootState: RootNavigationState,
    val state: AuthNavigationState,
) : AuthNavigator {
    override fun navigateRoot(rootNavKey: RootNavKey) {
        state.stack.clear()
        state.stack.add(state.startKey)
        rootState.stack.clear()
        rootState.stack.add(rootNavKey)
    }

    override fun navigate(key: NavKey) {
        state.stack.add(key)
    }

    override fun goBack(): Boolean {
        return if (state.stack.size > 1) {
            state.stack.removeLast()
            true
        } else {
            false
        }
    }
}
