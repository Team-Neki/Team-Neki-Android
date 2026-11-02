package com.neki.android.core.data.auth

import com.neki.android.core.data.remote.api.TokenRefreshService
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.model.Auth
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** One refresh request for the same credential pair, shared by normal API and analytics traffic. */
@Singleton
internal class TokenRefreshCoordinator @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val service: TokenRefreshService,
) {
    private val mutex = Mutex()
    private var lastRefresh: Refresh? = null

    suspend fun refresh(accessToken: String, refreshToken: String): Auth? = mutex.withLock {
        val expected = Auth(accessToken, refreshToken)
        val current = tokenRepository.getTokens() ?: return@withLock null
        if (current != expected) {
            return@withLock lastRefresh?.takeIf { it.previous == expected && it.updated == current }?.updated
        }
        // The server may rotate the refresh token, so finish saving it even if the caller times out.
        val updated = withContext(NonCancellable) {
            val response = service.refresh(expected.refreshToken)
            check(response.resultCode == "D-0") { "Unexpected refresh response" }
            response.data.toModel().takeIf {
                tokenRepository.replaceTokensIfCurrent(expected.accessToken, it.accessToken, it.refreshToken)
            }
        } ?: return@withLock null
        lastRefresh = Refresh(expected, updated)
        updated
    }

    private data class Refresh(val previous: Auth, val updated: Auth)
}
