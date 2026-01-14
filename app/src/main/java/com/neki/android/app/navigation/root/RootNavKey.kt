package com.neki.android.app.navigation.root

import androidx.navigation3.runtime.NavKey

sealed interface RootNavKey : NavKey {
    data object Login : RootNavKey
    data object Main : RootNavKey
}
