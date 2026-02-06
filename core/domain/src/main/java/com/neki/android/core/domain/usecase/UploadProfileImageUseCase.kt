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
            // null 이면 1, 2 과정 없이 바로 기본 프로필 이미지로 변경 요청
            userRepository.updateProfileImage(null).getOrThrow()
        } else {
            val fileName = ContentTypeUtil.generateFileName(contentType)

            // 1. 업로드 티켓 발급 (mediaId, presignedUrl)
            val (mediaId, presignedUrl) = mediaUploadRepository.getSingleUploadTicket(
                fileName = fileName,
                contentType = contentType.label,
                mediaType = MediaType.USER_PROFILE.name,
            ).getOrThrow()

            // 2. Presigned URL로 이미지 업로드
            mediaUploadRepository.uploadImageFromUri(
                uploadUrl = presignedUrl,
                uri = uri,
                contentType = contentType,
            ).getOrThrow()

            // 3. 프로필 이미지 갱신
            userRepository.updateProfileImage(mediaId).getOrThrow()
        }
    }
}
