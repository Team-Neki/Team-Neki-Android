package com.neki.android.core.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.neki.android.core.data.remote.api.ApiService
import com.neki.android.core.dataapi.repository.SampleRepository
import com.neki.android.core.model.Post
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
): SampleRepository {
    override suspend fun getPosts(): List<Post> {
        return apiService.getPosts()
            .map { it.toModel() }
    }

    override suspend fun getPost(
        id: Int
    ): Post {
        return apiService.getPost(id = id)
            .toModel()
    }


}