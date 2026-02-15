package com.neki.android.core.navigation.main

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.root.RootNavKey

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

interface MainNavigator {
    fun navigateRoot(rootNavKey: RootNavKey)
    fun navigate(key: NavKey)
    fun goBack()
    fun remove(key: NavKey)
}
