package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.analytics.ClientLogWithdrawal
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )
    fun hasTokens(): Flow<Boolean>
    fun getAccessToken(): Flow<String>
    fun getRefreshToken(): Flow<String>
    suspend fun clearTokensWithAuthCache(withdrawal: ClientLogWithdrawal? = null)
    fun pendingClientLogWithdrawals(): Flow<Set<ClientLogWithdrawal>>
    suspend fun clearClientLogWithdrawals(withdrawals: Set<ClientLogWithdrawal>)
}
