package com.neki.android.feature.mypage.impl.profile.model

import android.net.Uri

sealed interface EditProfileImageType {
    data class OriginalImageUrl(val url: String) : EditProfileImageType
    data class ImageUri(val uri: Uri) : EditProfileImageType
    data object Default : EditProfileImageType
}
