package com.neki.android.core.navigation.root

import androidx.navigation3.runtime.NavKey

interface RootNavigator {
    fun navigateToMain()
    fun navigateToAuth(startKey: NavKey)
}
