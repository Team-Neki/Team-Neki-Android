package com.neki.android.app.navigation.di

import com.neki.android.core.navigation.Navigator
import com.neki.android.core.navigation.NavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface AppModule {

    @Binds
    fun bindsNavigator(
        impl: NavigatorImpl,
    ): Navigator
}
