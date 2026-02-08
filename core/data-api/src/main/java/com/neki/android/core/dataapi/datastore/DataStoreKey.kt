package com.neki.android.core.dataapi.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKey {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val HAS_VISITED_RANDOM_POSE = booleanPreferencesKey("is_first_visit_random_pose")
}
