package com.neki.android.core.analytics.server

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject

internal class AnalyticsEnvironment @Inject constructor(@ApplicationContext context: Context) {
    private val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val appVersion: String = packageInfo.versionName ?: "unknown"
    val context: JsonObject = JsonObject(
        mapOf(
            "appBuild" to JsonPrimitive(packageInfo.longVersionCode.toString()),
            "osName" to JsonPrimitive("Android"),
            "osVersion" to JsonPrimitive(Build.VERSION.RELEASE),
            "deviceModel" to JsonPrimitive(Build.MODEL),
        ),
    )
}
