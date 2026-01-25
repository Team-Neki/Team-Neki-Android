package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Brand

interface PhotoBoothRepository {
    suspend fun getBrands(): Result<List<Brand>>
}
