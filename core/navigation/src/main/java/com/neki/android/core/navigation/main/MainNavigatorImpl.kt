package com.neki.android.core.navigation.main

import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class MainNavigatorImpl @Inject constructor(
    override val state: MainNavigationState,
) : MainNavigator {

    override fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()
            in state.topLevelKeys -> goToTopLevel(key)
            else -> goToKey(key)
        }
    }

    override fun goBack() {
        when (state.currentKey) {
            state.currentTopLevelKey -> state.topLevelStack.removeLastOrNull()
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    override fun remove(key: NavKey) {
        state.currentSubStack.apply {
            remove(key)
        }
    }

    override fun clear() {
        state.topLevelStack.clear()
        state.topLevelStack.add(state.startKey)
        state.subStacks.forEach { (key, stack) ->
            stack.clear()
            stack.add(key)
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
