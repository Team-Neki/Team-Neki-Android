package com.neki.android.core.domain.usecase

import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.extension.ContentTypeUtil
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
        imageUrl: String,
        contentType: ContentType = ContentType.JPEG,
        folderId: Long? = null,
    ): Result<Media> = runSuspendCatching {
        val fileName = ContentTypeUtil.generateFileName(contentType)

        // 1. 업로드 티켓 발급 (mediaId, presignedUrl)
        val (mediaId, presignedUrl) = mediaUploadRepository.getSingleUploadTicket(
            fileName = fileName,
            contentType = contentType.label,
            mediaType = MediaType.PHOTO_BOOTH.name,
        ).getOrThrow()

        // 2. Presigned URL로 이미지 업로드
        mediaUploadRepository.uploadImageFromUrl(
            uploadUrl = presignedUrl,
            imageUrl = imageUrl,
            contentType = contentType,
        ).getOrThrow()

        // 3. 사진 등록
        photoRepository.registerPhoto(
            mediaIds = listOf(mediaId),
            folderId = folderId,
        ).getOrThrow()

        return@runSuspendCatching Media(
            mediaId = mediaId,
            folderId = folderId,
            fileName = fileName,
            contentType = contentType,
        )
    }
}
