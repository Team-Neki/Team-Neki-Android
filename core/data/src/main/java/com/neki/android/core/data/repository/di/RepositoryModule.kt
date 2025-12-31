package com.neki.android.core.data.repository.di

import com.neki.android.core.data.repository.impl.SampleRepositoryImpl
import com.neki.android.core.dataapi.repository.SampleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindSampleRepositoryImpl(
        sampleRepositoryImpl: SampleRepositoryImpl,
    ): SampleRepository
}
