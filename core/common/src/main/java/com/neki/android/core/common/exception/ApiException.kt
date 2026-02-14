package com.neki.android.core.common.exception

open class NekiApiException(
    open val code: Int,
    override val message: String,
    override val cause: Throwable?,
) : Exception(message, cause)

class NoMorePoseException(
    override val code: Int,
    override val message: String,
    override val cause: Throwable?,
) : NekiApiException(code, message, cause)
