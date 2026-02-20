package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.AppVersion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppVersionResponse(
    @SerialName("platform") val platform: String,
    @SerialName("minVersion") val minVersion: String,
    @SerialName("currentVersion") val currentVersion: String,
) {
    fun toModel() = AppVersion(
        minVersion = this.minVersion,
        latestVersion = this.currentVersion,
    )
}
