package com.neki.android.core.data.sync

import android.content.Context
import androidx.work.WorkManager
import com.neki.android.core.dataapi.repository.OptimisticUpdateScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UpdateModule {
    @Binds
    fun bindOptimisticUpdateScheduler(impl: WorkManagerOptimisticUpdateScheduler): OptimisticUpdateScheduler

    companion object {
        @Provides
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
    }
}
