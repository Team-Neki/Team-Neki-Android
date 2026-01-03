package com.neki.android.core.data.repository.di

import com.neki.android.core.data.repository.impl.DataStoreRepositoryImpl
import com.neki.android.core.data.repository.impl.SampleRepositoryImpl
import com.neki.android.core.dataapi.repository.DataStoreRepository
import com.neki.android.core.dataapi.repository.SampleRepository
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
    fun bindSampleRepositoryImpl(
        sampleRepositoryImpl: SampleRepositoryImpl,
    ): SampleRepository

    @Binds
    @Singleton
    fun bindDataStoreRepositoryImpl(
        dataStoreRepositoryImpl: DataStoreRepositoryImpl,
    ): DataStoreRepository


}
