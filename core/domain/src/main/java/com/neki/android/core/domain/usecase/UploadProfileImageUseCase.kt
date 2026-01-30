package com.neki.android.core.domain.usecase

import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.UserRepository
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadProfileImageUseCase @Inject constructor(
    private val mediaUploadRepository: MediaUploadRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        nickname: String,
        contentType: ContentType = ContentType.JPEG,
    ): Result<Long> = runSuspendCatching {
        val fileName = generateFileName(contentType)

        // 1. 업로드 티켓 발급 (mediaId, presignedUrl)
        val (mediaId, presignedUrl) = mediaUploadRepository.getUploadTicket(
            fileName = fileName,
            contentType = contentType.label,
            mediaType = MediaType.USER_PROFILE.name,
        ).getOrThrow().first()

        // 2. Presigned URL로 이미지 업로드
        mediaUploadRepository.uploadImage(
            uploadUrl = presignedUrl,
            imageBytes = imageBytes,
            contentType = contentType,
        ).getOrThrow()

        // 3. 프로필 이미지 갱신
        userRepository.updateUserInfo(mediaId, nickname).getOrThrow()

        return@runSuspendCatching mediaId
    }

    private fun generateFileName(contentType: ContentType): String {
        val extension = when (contentType) {
            ContentType.JPEG -> "jpeg"
            ContentType.PNG -> "png"
        }
        return "${UUID.randomUUID()}.$extension"
    }
}
