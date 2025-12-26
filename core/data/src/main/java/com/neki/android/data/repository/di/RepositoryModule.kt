package com.neki.android.data.repository.di

import com.example.dataapi.repository.SampleRepository
import com.neki.android.data.repository.impl.SampleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindSampleRepositoryImpl(
        sampleRepositoryImpl: SampleRepositoryImpl
    ): SampleRepository


}