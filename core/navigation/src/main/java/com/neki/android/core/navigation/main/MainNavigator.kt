package com.neki.android.core.navigation.main

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

interface MainNavigator {
    fun navigate(key: NavKey)
    fun navigateToLogin(loginKey: NavKey)
    fun goBack()
    fun remove(key: NavKey)
}
