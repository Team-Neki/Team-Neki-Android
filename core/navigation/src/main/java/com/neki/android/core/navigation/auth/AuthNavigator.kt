package com.neki.android.core.navigation.auth

import com.neki.android.core.navigation.Navigator

interface AuthNavigator : Navigator {
    val state: AuthNavigationState
}
