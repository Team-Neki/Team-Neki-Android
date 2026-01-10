package com.neki.android.core.data.repository.di

import com.neki.android.core.data.repository.impl.AuthRepositoryImpl
import com.neki.android.core.data.repository.impl.DataStoreRepositoryImpl
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.DataStoreRepository
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

}
