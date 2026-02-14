package com.neki.android.core.domain.usecase

import android.net.Uri
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.domain.extension.ContentTypeUtil
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadProfileImageUseCase @Inject constructor(
    private val mediaUploadRepository: MediaUploadRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        uri: Uri?,
        contentType: ContentType = ContentType.JPEG,
    ): Result<Unit> = runSuspendCatching {
        if (uri == null) {
            // null 이면 바로 기본 프로필 이미지로 변경 요청
            userRepository.updateProfileImage(null).getOrThrow()
        } else {
            val fileName = ContentTypeUtil.generateFileName(contentType)

            val mediaId = mediaUploadRepository.uploadImageFromUri(
                fileName = fileName,
                uri = uri,
                contentType = contentType,
                mediaType = MediaType.USER_PROFILE,
            ).getOrThrow()

            userRepository.updateProfileImage(mediaId).getOrThrow()
        }
    }
}
