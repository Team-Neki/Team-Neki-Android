package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.ApiService
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.SampleRepository
import com.neki.android.core.model.Post
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : SampleRepository {
    override suspend fun getPosts(): Result<List<Post>> = runSuspendCatching {
        apiService.getPosts().map { it.toModel() }
    }

    override suspend fun getPost(id: Int): Result<Post> = runSuspendCatching {
        apiService.getPost(id).toModel()
    }
}
