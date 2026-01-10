package com.neki.android.core.data.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse<T>(
    val resultCode: String,
    val message: String,
    val success: Boolean,
    val data: T
)
