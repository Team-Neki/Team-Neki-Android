package com.neki.android.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

interface Navigator {
    fun navigate(key: NavKey)
    fun goBack()
}
