package com.neki.android.feature.photo_upload.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface PhotoUploadNavKey : NavKey {

    @Serializable
    data object QRScan : PhotoUploadNavKey

    @Serializable
    data class UploadAlbum(
        val imageUrl: String? = null,
        val uriStrings: List<String> = emptyList(),
    ) : PhotoUploadNavKey
}

fun Navigator.navigateToQRScan() {
    navigate(PhotoUploadNavKey.QRScan)
}

fun Navigator.navigateToUploadAlbum(uriStrings: List<String>) {
    navigate(PhotoUploadNavKey.UploadAlbum(uriStrings = uriStrings))
}

fun Navigator.navigateToUploadAlbum(imageUrl: String) {
    navigate(PhotoUploadNavKey.UploadAlbum(imageUrl = imageUrl))
}
