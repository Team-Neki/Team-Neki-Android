package com.neki.android.core.navigation.main

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.Navigator

typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

interface MainNavigator : Navigator
