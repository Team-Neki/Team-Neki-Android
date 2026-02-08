package com.neki.android.core.navigation.root

import androidx.navigation3.runtime.NavKey

sealed interface RootNavKey : NavKey {
    data object Auth : RootNavKey
    data object Main : RootNavKey
}
