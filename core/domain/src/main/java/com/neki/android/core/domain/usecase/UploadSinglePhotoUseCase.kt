package com.neki.android.core.domain.usecase

import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.Media
import com.neki.android.core.model.MediaType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadSinglePhotoUseCase @Inject constructor(
    private val mediaUploadRepository: MediaUploadRepository,
    private val photoRepository: PhotoRepository,
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        contentType: ContentType = ContentType.JPEG,
        folderId: Long? = null,
    ): Result<Media> = runSuspendCatching {
        val fileName = generateFileName(contentType)

        // 1. 업로드 티켓 발급 (mediaId, presignedUrl)
        val (mediaId, presignedUrl) = mediaUploadRepository.getUploadTicket(
            fileName = fileName,
            contentType = contentType.label,
            mediaType = MediaType.PHOTO_BOOTH.name,
        ).getOrThrow()

        // 2. Presigned URL로 이미지 업로드
        mediaUploadRepository.uploadImage(
            uploadUrl = presignedUrl,
            imageBytes = imageBytes,
            contentType = contentType,
        ).getOrThrow()

        // 3. 사진 등록
        photoRepository.registerPhoto(
            mediaId = mediaId,
            folderId = folderId,
        ).getOrThrow()

        return@runSuspendCatching Media(
            mediaId = mediaId,
            folderId = folderId,
            fileName = fileName,
            contentType = contentType,
        )
    }

    private fun generateFileName(contentType: ContentType): String {
        val extension = when (contentType) {
            ContentType.JPEG -> "jpg"
            ContentType.PNG -> "png"
        }
        return "${UUID.randomUUID()}.$extension"
    }
}
