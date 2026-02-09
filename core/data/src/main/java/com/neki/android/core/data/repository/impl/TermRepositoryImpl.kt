package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.TermService
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.TermRepository
import com.neki.android.core.model.Term
import javax.inject.Inject

class TermRepositoryImpl @Inject constructor(
    private val termService: TermService,
) : TermRepository {
    override suspend fun getTerms(): Result<List<Term>> = runSuspendCatching {
        termService.getTerms().data.toModels()
    }
}
