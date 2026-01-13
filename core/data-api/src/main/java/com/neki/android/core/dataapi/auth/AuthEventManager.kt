package com.neki.android.core.dataapi.auth

import kotlinx.coroutines.flow.SharedFlow

sealed class AuthEvent {
    data object RefreshTokenExpired : AuthEvent()
}

interface AuthEventManager {
    val authEvent: SharedFlow<AuthEvent>
    fun emitTokenExpired()
}
