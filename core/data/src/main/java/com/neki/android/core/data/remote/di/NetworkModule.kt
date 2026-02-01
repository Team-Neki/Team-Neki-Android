package com.neki.android.core.data.remote.di

import com.neki.android.core.common.const.Const.TAG_REST_API
import com.neki.android.core.data.BuildConfig
import com.neki.android.core.data.remote.api.AuthService
import com.neki.android.core.data.remote.model.request.RefreshTokenRequest
import com.neki.android.core.data.remote.model.response.AuthResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.qualifier.UploadHttpClient
import com.neki.android.core.dataapi.auth.AuthCacheManager
import com.neki.android.core.dataapi.auth.AuthEventManager
import com.neki.android.core.dataapi.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    val BASE_URL = BuildConfig.BASE_URL
    const val TIME_OUT = 5000L
    const val UPLOAD_TIME_OUT = 10_000L

    val sendWithoutAuthUrls = listOf(
        "/api/auth/kakao/login",
        "/api/auth/refresh",
    )

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideAuthService(
        client: HttpClient,
    ): AuthService = AuthService(client)

    @Provides
    @Singleton
    fun provideAuthCacheManager(
        httpClient: HttpClient,
    ): AuthCacheManager = object : AuthCacheManager {
        override fun invalidateTokenCache() {
            httpClient.plugin(Auth).providers
                .filterIsInstance<BearerAuthProvider>()
                .firstOrNull()
                ?.clearToken()
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        tokenRepository: TokenRepository,
        authEventManager: AuthEventManager,
    ): HttpClient {
        return HttpClient(Android) {
            install(DefaultRequest) {
                url(BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        if (tokenRepository.isSavedTokens().first()) {
                            BearerTokens(
                                accessToken = tokenRepository.getAccessToken().first(),
                                refreshToken = tokenRepository.getRefreshToken().first(),
                            )
                        } else null
                    }

                    refreshTokens {
                        if (oldTokens != null) {
                            return@refreshTokens try {
                                val response = client.post("/api/auth/refresh") {
                                    setBody(
                                        RefreshTokenRequest(
                                            refreshToken = tokenRepository.getRefreshToken().first(),
                                        ),
                                    )
                                }.body<BasicResponse<AuthResponse>>()

                                tokenRepository.saveTokens(
                                    accessToken = response.data.accessToken,
                                    refreshToken = response.data.refreshToken,
                                )

                                BearerTokens(
                                    accessToken = response.data.accessToken,
                                    refreshToken = response.data.refreshToken,
                                )
                            } catch (e: Exception) {
                                Timber.e(e)
                                tokenRepository.clearTokens()
                                authEventManager.emitTokenExpired()
                                null
                            }
                        } else null
                    }

                    sendWithoutRequest { request ->
                        val shouldNotAuth = sendWithoutAuthUrls.any {
                            request.url.encodedPath == it
                        }
                        !shouldNotAuth
                    }
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag(TAG_REST_API).d(message)
                    }
                }

                level = LogLevel.BODY
            }

            install(HttpTimeout) {
                connectTimeoutMillis = TIME_OUT
                requestTimeoutMillis = TIME_OUT
            }

            expectSuccess = true
        }
    }

    @UploadHttpClient
    @Provides
    @Singleton
    fun provideUploadHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag(TAG_REST_API).d(message)
                    }
                }

                level = LogLevel.HEADERS
            }

            install(HttpTimeout) {
                connectTimeoutMillis = UPLOAD_TIME_OUT
                requestTimeoutMillis = UPLOAD_TIME_OUT
            }

            expectSuccess = true
        }
    }
}
