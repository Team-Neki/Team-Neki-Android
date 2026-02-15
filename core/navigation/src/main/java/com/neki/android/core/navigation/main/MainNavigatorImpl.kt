package com.neki.android.core.navigation.main

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.auth.AuthNavigationState
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.core.navigation.root.RootNavigationState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class MainNavigatorImpl @Inject constructor(
    private val rootState: RootNavigationState,
    private val authState: AuthNavigationState,
    val mainState: MainNavigationState,
) : MainNavigator {
    override fun navigateRoot(rootNavKey: RootNavKey) {
        clearMainStack()
        when (rootNavKey) {
            is RootNavKey.Auth -> {
                authState.stack.clear()
                authState.stack.add(rootNavKey.startKey)
            }
            is RootNavKey.Main -> {}
        }
        rootState.stack.clear()
        rootState.stack.add(rootNavKey)
    }

    private fun clearMainStack() {
        this@MainNavigatorImpl.mainState.topLevelStack.clear()
        mainState.topLevelStack.add(mainState.startKey)
        mainState.subStacks.forEach { (key, stack) ->
            stack.clear()
            stack.add(key)
        }
    }

    override fun navigate(key: NavKey) {
        when (key) {
            mainState.currentTopLevelKey -> clearSubStack()
            in mainState.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    override fun goBack() {
        when (mainState.currentKey) {
            mainState.currentTopLevelKey -> mainState.topLevelStack.removeLastOrNull()
            else -> mainState.currentSubStack.removeLastOrNull()
        }
    }

    private fun goToKey(key: NavKey) {
        mainState.currentSubStack.apply {
            remove(key)
            add(key)
        }
    }

    private fun goToTopLevel(key: NavKey) {
        mainState.topLevelStack.apply {
            if (key == mainState.startKey) clear()
            else remove(key)
            add(key)
        }
    }

    private fun clearSubStack() {
        mainState.currentSubStack.run {
            if (size > 1) subList(1, size).clear()
        }
    }

    override fun remove(key: NavKey) {
        mainState.currentSubStack.apply {
            remove(key)
        }
    }
}
