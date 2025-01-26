package com.tibame.foodhunter.sharon.di

import com.tibame.foodhunter.di.AppConfiguration
import com.tibame.foodhunter.sharon.data.datasource.remote.source.RemoteNoteDataSourceImpl
import com.tibame.foodhunter.sharon.data.repository.FakeNoteRepositoryImpl
import com.tibame.foodhunter.sharon.data.repository.NoteRepositoryImpl
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideNoteRepository(
        appConfiguration: AppConfiguration,
        dataSourceImpl: RemoteNoteDataSourceImpl,
    ): NoteRepository {
        return if (appConfiguration.isTestMode == false) {
            FakeNoteRepositoryImpl()
        } else {
            NoteRepositoryImpl(dataSourceImpl)
        }
    }
}