package com.neki.android.core.data.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.neki.android.core.data.local.model.MarketingPopupRecord
import com.neki.android.core.data.local.serializer.MarketingPopupRecordSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

private const val AUTH_DATASTORE = "auth_datastore"
private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = AUTH_DATASTORE)

private const val TOKEN_DATASTORE = "token_datastore"
private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = TOKEN_DATASTORE)

private const val USER_DATASTORE = "user_datastore"
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

@InstallIn(SingletonComponent::class)
@Module
internal object DataStoreModule {

    @AuthDataStore
    @Singleton
    @Provides
    fun provideAuthDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.authDataStore

    @TokenDataStore
    @Singleton
    @Provides
    fun provideTokenDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.tokenDataStore

    @UserDataStore
    @Singleton
    @Provides
    fun provideUserDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.userDataStore

    @MarketingPopupDataStore
    @Singleton
    @Provides
    fun provideMarketingPopupDataStore(
        @ApplicationContext context: Context,
    ): DataStore<MarketingPopupRecord> = DataStoreFactory.create(
        serializer = MarketingPopupRecordSerializer,
        produceFile = { File(context.filesDir, "datastore/marketing_popup_record.json") },
        corruptionHandler = ReplaceFileCorruptionHandler { MarketingPopupRecord() },
    )
}
