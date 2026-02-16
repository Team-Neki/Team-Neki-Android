package com.neki.android.app.navigation.di

import com.neki.android.app.navigation.keys.START_AUTH_NAV_KEY
import com.neki.android.app.navigation.keys.START_MAIN_NAV_KEY
import com.neki.android.app.navigation.keys.START_ROOT_NAV_KEY
import com.neki.android.app.navigation.keys.TOP_LEVEL_MAIN_NAV_KEYS
import com.neki.android.core.navigation.auth.AuthNavigationState
import com.neki.android.core.navigation.main.MainNavigationState
import com.neki.android.core.navigation.root.RootNavigationState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal object NavigationStateModule {

    @Provides
    @ActivityRetainedScoped
    fun providesRootNavigationState(): RootNavigationState {
        return RootNavigationState(
            startKey = START_ROOT_NAV_KEY,
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun providesAuthNavigationState(): AuthNavigationState {
        return AuthNavigationState(
            startKey = START_AUTH_NAV_KEY,
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun providesMainNavigationState(): MainNavigationState {
        return MainNavigationState(
            startKey = START_MAIN_NAV_KEY,
            topLevelKeys = TOP_LEVEL_MAIN_NAV_KEYS.toSet(),
        )
    }
}
