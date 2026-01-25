package com.neki.android.core.data.repository.di

import com.neki.android.core.data.auth.AuthEventManagerImpl
import com.neki.android.core.data.repository.impl.AuthRepositoryImpl
import com.neki.android.core.data.repository.impl.DataStoreRepositoryImpl
import com.neki.android.core.data.repository.impl.MediaUploadRepositoryImpl
import com.neki.android.core.data.repository.impl.PhotoRepositoryImpl
import com.neki.android.core.data.repository.impl.TokenRepositoryImpl
import com.neki.android.core.dataapi.auth.AuthEventManager
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.DataStoreRepository
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.dataapi.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindDataStoreRepositoryImpl(
        dataStoreRepositoryImpl: DataStoreRepositoryImpl,
    ): DataStoreRepository

    @Binds
    @Singleton
    fun bindAuthRepositoryImpl(
        authRepositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    fun bindTokenRepositoryImpl(
        tokenRepositoryImpl: TokenRepositoryImpl,
    ): TokenRepository

    @Binds
    @Singleton
    fun bindAuthEventManagerImpl(
        authEventManagerImpl: AuthEventManagerImpl,
    ): AuthEventManager

    @Binds
    @Singleton
    fun bindMediaUploadRepositoryImpl(
        mediaUploadRepositoryImpl: MediaUploadRepositoryImpl,
    ): MediaUploadRepository

    @Binds
    @Singleton
    fun bindPhotoRepositoryImpl(
        photoRepositoryImpl: PhotoRepositoryImpl,
    ): PhotoRepository
}
