package com.neki.android.app.navigation.di

import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.core.navigation.main.MainNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface AppModule {

    @Binds
    fun bindsMainNavigator(
        mainNavigatorImpl: MainNavigatorImpl,
    ): MainNavigator
}
