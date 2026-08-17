package com.neki.android.app.di

import com.neki.android.app.BuildConfig
import com.neki.android.core.analytics.initializer.AmplitudeApiKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AnalyticsConfigModule {

    @Provides
    @AmplitudeApiKey
    fun provideAmplitudeApiKey(): String = BuildConfig.AMPLITUDE_API_KEY
}
