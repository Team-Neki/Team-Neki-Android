package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.PhotoBoothService
import com.neki.android.core.data.remote.model.response.toModels
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.PhotoBoothRepository
import com.neki.android.core.model.Brand
import javax.inject.Inject

class PhotoBoothRepositoryImpl @Inject constructor(
    private val photoBoothService: PhotoBoothService,
) : PhotoBoothRepository {
    override suspend fun getBrands(): Result<List<Brand>> = runSuspendCatching {
        photoBoothService.getBrands().data.toModels()
    }
}
