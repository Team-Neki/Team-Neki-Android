package com.neki.android.core.dataapi.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKey {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
}
