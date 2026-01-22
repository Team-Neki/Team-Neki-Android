package com.neki.android.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.root.RootNavKey

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

interface Navigator {
    fun navigateRoot(rootNavKey: RootNavKey)
    fun navigate(key: NavKey)
    fun goBack()
    fun remove(key: NavKey)
}
