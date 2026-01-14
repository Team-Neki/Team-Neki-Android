package com.neki.android.core.navigation

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.core.navigation.root.RootNavigationState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class NavigatorImpl @Inject constructor(
    private val rootState: RootNavigationState,
    val state: NavigationState,
) : Navigator {
    override fun navigateRoot(rootNavKey: RootNavKey) {
        rootState.stack.clear()
        rootState.stack.add(rootNavKey)
    }

    override fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    override fun goBack() {
        when (state.currentKey) {
            state.startKey -> error("You cannot go back from the start route")
            state.currentTopLevelKey -> {
                state.topLevelStack.removeLastOrNull()
            }

            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) clear()
            else remove(key)
            add(key)
        }
    }

    private fun clearSubStack() {
        state.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}
