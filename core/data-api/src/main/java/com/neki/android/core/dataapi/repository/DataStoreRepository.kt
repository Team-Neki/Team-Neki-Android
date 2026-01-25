package com.neki.android.core.dataapi.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean)
    fun getBoolean(key: Preferences.Key<Boolean>): Flow<Boolean>
}
