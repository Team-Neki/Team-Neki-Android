package com.neki.android.feature.map.impl.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.neki.android.feature.map.impl.LocLatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Result<LocLatLng> =
        suspendCancellableCoroutine { coroutine ->
            val cancellationTokenSource = CancellationTokenSource()

            coroutine.invokeOnCancellation { cancellationTokenSource.cancel() }

            LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    location?.let {
                        coroutine.resume(Result.success(LocLatLng(it.latitude, it.longitude)))
                    } ?: coroutine.resume(Result.failure(Exception("Location is null")))
                }
                .addOnFailureListener { e ->
                    coroutine.resume(Result.failure(e))
                }
        }
}
