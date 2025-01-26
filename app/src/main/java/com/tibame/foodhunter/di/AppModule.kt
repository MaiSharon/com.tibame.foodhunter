package com.tibame.foodhunter.di

import android.content.Context
import com.tibame.foodhunter.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

data class AppConfiguration (
    val isTestMode: Boolean,
    val apiUrl: String
)



@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    @Singleton
    fun provideAppConfiguration(
        @ApplicationContext context: Context
    ) : AppConfiguration {
        return AppConfiguration(
            isTestMode = BuildConfig.DEBUG,
            apiUrl = BuildConfig.BASE_URL
        )
    }
}



