package com.neki.android.core.data.remote.di

import com.neki.android.core.common.const.Const.TAG_REST_API
import com.neki.android.core.data.BuildConfig
import com.neki.android.core.data.remote.api.ApiService
import com.neki.android.core.data.remote.model.request.RefreshTokenRequest
import com.neki.android.core.data.remote.model.response.AuthResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.dataapi.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
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

    const val BASE_URL = BuildConfig.BASE_URL
    const val TIME_OUT = 5000L

    val sendWithoutJwtUrls = listOf(
        "/api/auth/kakao/login",
        "/api/auth/refresh"
    )

    @Provides
    @Singleton
    fun provideApiService(
        client: HttpClient,
    ): ApiService = ApiService(client)

    @Provides
    @Singleton
    fun provideHttpClient(
        dataStoreRepository: DataStoreRepository,
    ): HttpClient {
        return HttpClient(Android) {
            install(DefaultRequest) {
                url(BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        Timber.d("BearerAuth - loadTokens")
                        if (dataStoreRepository.isSavedJwtTokens().first()) {
                            BearerTokens(
                                accessToken = dataStoreRepository.getAccessToken().first()!!,
                                refreshToken = dataStoreRepository.getRefreshToken().first()!!
                            )
                        } else null
                    }

                    refreshTokens {
                        Timber.d("BearerAuth - AccessToken 갱신 시도")
                        Timber.d("RefreshToken : ${dataStoreRepository.getRefreshToken().first()}")
                        if (oldTokens != null) {
                            return@refreshTokens try {
                                val response = client.post("/api/auth/refresh") {
                                    setBody(
                                        RefreshTokenRequest(
                                            refreshToken = dataStoreRepository.getRefreshToken().first()!!
                                        )
                                    )
                                }.body<BasicResponse<AuthResponse>>()

                                dataStoreRepository.saveJwtTokens(
                                    accessToken = response.data.accessToken,
                                    refreshToken = response.data.refreshToken
                                )

                                Timber.d("New AccessToken : ${response.data.accessToken}")
                                Timber.d("New RefreshToken : ${response.data.refreshToken}")

                                BearerTokens(
                                    accessToken = response.data.accessToken,
                                    refreshToken = response.data.refreshToken,
                                )
                            } catch (e: Exception) {
                                Timber.e(e)
                                dataStoreRepository.clearTokens()
                                null
                            }
                        } else null
                    }

                    sendWithoutRequest { request ->
                        val shouldNotJwt = sendWithoutJwtUrls.any {
                            request.url.encodedPath == it
                        }

                        Timber.d("Bearer 인증 필요 API 여부 : $shouldNotJwt")
                        !shouldNotJwt
                    }
                }
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
}
