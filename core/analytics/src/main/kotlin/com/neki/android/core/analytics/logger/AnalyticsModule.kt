package com.neki.android.core.analytics.logger

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.AutocaptureOption
import com.facebook.appevents.AppEventsLogger
import com.neki.android.core.analytics.server.AnalyticsEnvironment
import com.neki.android.core.dataapi.repository.ClientLogRepository
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
    abstract fun bindAnalyticsLogger(impl: NekiAnalyticsLogger): AnalyticsLogger

    @Binds
    @Singleton
    abstract fun bindAmplitudeAnalyticsClient(impl: AmplitudeSdkAnalyticsClient): AmplitudeAnalyticsClient

    @Binds
    @Singleton
    abstract fun bindMetaAnalyticsLogger(impl: FacebookMetaAnalyticsLogger): MetaAnalyticsLogger

    @Binds
    @Singleton
    abstract fun bindMetaAnalyticsClient(impl: FacebookSdkMetaAnalyticsClient): MetaAnalyticsClient

    companion object {
        @Provides
        @Singleton
        fun provideServerAnalyticsLogger(
            client: AmplitudeAnalyticsClient,
            sessions: AmplitudeSessionRegistry,
            repository: Lazy<ClientLogRepository>,
            environment: Lazy<AnalyticsEnvironment>,
        ): ServerAnalyticsLogger = ServerAnalyticsLogger(
            client,
            sessions,
            lazy { repository.get() },
            lazy { environment.get().appVersion },
            lazy { environment.get().context },
        )

        @Provides
        @Singleton
        fun provideAmplitude(
            @AmplitudeApiKey apiKey: String,
            @ApplicationContext context: Context,
            sessions: AmplitudeSessionRegistry,
        ): Amplitude = Amplitude(apiKey, context) {
            minIdLength = 1
            autocapture = setOf(
                AutocaptureOption.SESSIONS,
                AutocaptureOption.APP_LIFECYCLES,
            )
        }.apply { add(AmplitudeSessionPlugin(sessions)) }

        @Provides
        @Singleton
        fun provideAppEventsLogger(
            @ApplicationContext context: Context,
        ): AppEventsLogger = AppEventsLogger.newLogger(context)

        @Provides
        @Singleton
        fun provideAnalyticsInitializer(
            amplitude: Lazy<Amplitude>,
            logger: Lazy<ServerAnalyticsLogger>,
        ): AnalyticsInitializer = AnalyticsInitializer {
            amplitude.get()
            logger.get().initialize()
        }
    }
}
