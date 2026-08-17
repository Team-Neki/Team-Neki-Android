package com.neki.android.core.analytics.logger

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.AutocaptureOption
import com.neki.android.core.analytics.initializer.AmplitudeApiKey
import com.neki.android.core.analytics.initializer.AnalyticsInitializer
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsLogger(impl: AmplitudeAnalyticsLogger): AnalyticsLogger

    @Binds
    @Singleton
    abstract fun bindAmplitudeAnalyticsClient(impl: AmplitudeSdkAnalyticsClient): AmplitudeAnalyticsClient

    companion object {
        @Provides
        @Singleton
        fun provideAmplitude(
            @AmplitudeApiKey apiKey: String,
            @ApplicationContext context: Context,
        ): Amplitude = Amplitude(apiKey, context) {
            minIdLength = 1
            autocapture = setOf(
                AutocaptureOption.SESSIONS,
                AutocaptureOption.APP_LIFECYCLES,
            )
        }

        @Provides
        @Singleton
        fun provideAnalyticsInitializer(
            amplitude: Lazy<Amplitude>,
        ): AnalyticsInitializer = AnalyticsInitializer(amplitude::get)
    }
}
