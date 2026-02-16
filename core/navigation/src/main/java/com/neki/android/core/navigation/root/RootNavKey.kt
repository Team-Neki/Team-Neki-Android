package com.neki.android.core.navigation.root

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface RootNavKey : NavKey {
    @Serializable
    data class Auth(val startKey: NavKey) : RootNavKey

    @Serializable
    data object Main : RootNavKey
}
