package com.neki.android.core.data.auth

import com.neki.android.core.dataapi.auth.AuthEvent
import com.neki.android.core.dataapi.auth.AuthEventManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventManagerImpl @Inject constructor() : AuthEventManager {
    private val _authEvent = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    override val authEvent: SharedFlow<AuthEvent> = _authEvent.asSharedFlow()

    override fun emitTokenExpired() {
        _authEvent.tryEmit(AuthEvent.RefreshTokenExpired)
    }
}
