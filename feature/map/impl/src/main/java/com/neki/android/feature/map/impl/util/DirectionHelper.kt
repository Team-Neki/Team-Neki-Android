package com.neki.android.feature.map.impl.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri

internal object DirectionHelper {
    fun moveAppOrStore(
        context: Context,
        url: String,
        packageName: String,
    ) {
        val intent = if (isInstalled(context.packageManager, packageName)) {
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                setPackage(packageName)
            }
        } else {
            Intent(
                Intent.ACTION_VIEW,
                "market://details?id=$packageName".toUri(),
            )
        }
        context.startActivity(intent)
    }

    private fun isInstalled(
        packageManager: PackageManager,
        packageName: String,
    ): Boolean = runCatching { packageManager.getPackageInfo(packageName, 0) }.isSuccess
}
