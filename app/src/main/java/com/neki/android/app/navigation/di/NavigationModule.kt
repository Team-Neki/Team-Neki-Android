package com.neki.android.app.navigation.di

import com.neki.android.app.navigation.keys.START_NAV_KEY
import com.neki.android.app.navigation.keys.TOP_LEVEL_NAV_KEYS
import com.neki.android.core.navigation.NavigationState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal object NavigationModule {

    @Provides
    @ActivityRetainedScoped
    fun providesNavigationState(): NavigationState {
        return NavigationState(
            startKey = START_NAV_KEY,
            topLevelKeys = TOP_LEVEL_NAV_KEYS.toSet(),
        )
    }
}
