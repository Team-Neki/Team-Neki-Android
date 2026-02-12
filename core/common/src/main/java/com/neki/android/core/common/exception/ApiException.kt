package com.neki.android.core.common.exception

sealed class NekiApiException(
    open val code: Int,
    override val message: String,
    override val cause: Throwable?,
) : Exception(message, cause) {

    class NoMorePoseException(
        override val code: Int,
        override val message: String,
        override val cause: Throwable? = null,
    ) : NekiApiException(code, message, cause)
}
