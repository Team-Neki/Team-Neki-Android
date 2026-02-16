package com.neki.android.app.navigation.di

import com.neki.android.core.navigation.auth.AuthNavigator
import com.neki.android.core.navigation.auth.AuthNavigatorImpl
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.core.navigation.main.MainNavigatorImpl
import com.neki.android.core.navigation.root.RootNavigator
import com.neki.android.core.navigation.root.RootNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface NavigatorModule {

    @Binds
    fun bindRootNavigator(impl: RootNavigatorImpl): RootNavigator

    @Binds
    fun bindAuthNavigator(impl: AuthNavigatorImpl): AuthNavigator

    @Binds
    fun bindMainNavigator(impl: MainNavigatorImpl): MainNavigator
}
