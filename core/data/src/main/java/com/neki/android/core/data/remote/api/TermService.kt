package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.TermsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class TermService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getTerms(): BasicResponse<TermsResponse> {
        return client.get("/api/terms").body()
    }
}
