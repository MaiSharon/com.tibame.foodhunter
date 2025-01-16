package com.tibame.foodhunter.sharon.di

import com.tibame.foodhunter.sharon.data.datasource.remote.api.NoteApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module  // 標記這是一個 Hilt 依賴注入模組
@InstallIn(SingletonComponent::class)  // 在整個 App 生命週期中都是單例
object NoteModule {
    @Provides  // 告訴 Hilt 這個方法提供依賴
    @Singleton  // 讓這個提供的實例是單例
    fun provideNoteApiService(retrofit: Retrofit): NoteApiService {
        // 使用 Retrofit 創建 API 服務介面的實現
        return retrofit.create(NoteApiService::class.java)
    }
}

