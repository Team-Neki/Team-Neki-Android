package com.neki.android.core.data.repository.di

import com.neki.android.core.data.auth.AuthEventManagerImpl
import com.neki.android.core.data.repository.impl.AuthRepositoryImpl
import com.neki.android.core.data.repository.impl.MediaUploadRepositoryImpl
import com.neki.android.core.data.repository.impl.FolderRepositoryImpl
import com.neki.android.core.data.repository.impl.MapRepositoryImpl
import com.neki.android.core.data.repository.impl.PhotoRepositoryImpl
import com.neki.android.core.data.repository.impl.PoseRepositoryImpl
import com.neki.android.core.data.repository.impl.TermRepositoryImpl
import com.neki.android.core.data.repository.impl.TokenRepositoryImpl
import com.neki.android.core.data.repository.impl.UserRepositoryImpl
import com.neki.android.core.dataapi.auth.AuthEventManager
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.MapRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.dataapi.repository.PoseRepository
import com.neki.android.core.dataapi.repository.TermRepository
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.dataapi.repository.UserRepository
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
    fun bindAuthRepositoryImpl(
        authRepositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    fun bindUserRepositoryImpl(
        userRepositoryImpl: UserRepositoryImpl,
    ): UserRepository

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

    @Binds
    @Singleton
    fun bindFolderRepositoryImpl(
        folderRepositoryImpl: FolderRepositoryImpl,
    ): FolderRepository

    @Binds
    @Singleton
    fun bindMapRepositoryImpl(
        mapRepositoryImpl: MapRepositoryImpl,
    ): MapRepository

    @Binds
    @Singleton
    fun bindPoseRepositoryImpl(
        poseRepositoryImpl: PoseRepositoryImpl,
    ): PoseRepository

    @Binds
    @Singleton
    fun bindTermRepositoryImpl(
        termRepositoryImpl: TermRepositoryImpl,
    ): TermRepository
}
