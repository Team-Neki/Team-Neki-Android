package com.neki.android.core.data.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse<T>(
    val resultCode: String,
    val message: String,
    val data: T,
)

@Serializable
data class BasicNullableResponse<T>(
    val resultCode: String,
    val message: String,
    val data: T?,
)
