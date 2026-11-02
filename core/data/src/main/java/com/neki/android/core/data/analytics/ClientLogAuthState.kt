package com.neki.android.core.data.analytics

import javax.inject.Inject
import javax.inject.Singleton

/** Credentials are bound to an explicitly restored/login user, never inferred from a queued event. */
@Singleton
class ClientLogAuthState @Inject constructor() {
    private var credentials: Credentials? = null

    @Synchronized
    fun updateTokens(accessToken: String, refreshToken: String, isRefresh: Boolean) {
        credentials = Credentials(
            userId = if (isRefresh) credentials?.userId else null,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    @Synchronized
    fun setUserId(userId: String?) {
        credentials = credentials?.copy(userId = userId)
    }

    @Synchronized
    fun clear() {
        credentials = null
    }

    @Synchronized
    fun snapshot(): Credentials? = credentials

    data class Credentials(
        val userId: String?,
        val accessToken: String,
        val refreshToken: String,
    )
}
