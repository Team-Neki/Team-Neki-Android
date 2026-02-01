package com.neki.android.feature.mypage.impl.profile.model

import android.net.Uri

sealed interface SelectedProfileImage {
    data object NoChange : SelectedProfileImage
    data class Selected(val uri: Uri?) : SelectedProfileImage
}
