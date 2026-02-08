package com.neki.android.core.common.exception

class ServerApiException(
    val code: Int,
    message: String,
) : Throwable(message)

class ClientApiException(
    val code: Int,
    message: String,
) : Throwable(message)
