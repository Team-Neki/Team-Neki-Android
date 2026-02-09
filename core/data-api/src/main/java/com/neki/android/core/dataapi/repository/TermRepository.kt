package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Term

interface TermRepository {
    suspend fun getTerms(): Result<List<Term>>
    suspend fun agreeTerms(termIds: List<Long>): Result<Unit>
}
