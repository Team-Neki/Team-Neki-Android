package com.neki.android.core.navigation

import androidx.navigation3.runtime.NavKey

interface Navigator {
    fun navigate(key: NavKey)
    fun goBack()
}
