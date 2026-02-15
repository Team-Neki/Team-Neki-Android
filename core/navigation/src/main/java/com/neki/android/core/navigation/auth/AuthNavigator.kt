package com.neki.android.core.navigation.auth

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.root.RootNavKey

interface AuthNavigator {
    fun navigateRoot(rootNavKey: RootNavKey)
    fun navigate(key: NavKey)
    fun goBack()
    fun remove(key: NavKey)
}
