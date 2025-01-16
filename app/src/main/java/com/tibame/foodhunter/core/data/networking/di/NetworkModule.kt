package com.tibame.foodhunter.core.data.networking.di

import com.tibame.foodhunter.BuildConfig
import com.tibame.foodhunter.core.data.networking.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Session cookie 驗證用
     * @return 用於處理認證的攔截器
     */
    @Provides
    @Singleton
    fun provideCookieInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val url = request.url.toString()

            // 只對 notes 相關 API 加入 Cookie
            if (url.contains("/api/v1/notes")) {
                val sessionCookie = SessionManager.getSessionCookie()
                sessionCookie?.let {
                    val newRequest = request.newBuilder()
                        .addHeader("Cookie", it)
                        .build()
                    chain.proceed(newRequest)
                } ?: chain.proceed(request)
            } else {
                chain.proceed(request)
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cookieInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(cookieInterceptor)  // 加入 Cookie 攔截器
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 提供 Retrofit 實例
     * Retrofit 是用於將 HTTP API 轉換為 Kotlin 介面的函式庫
     * @param okHttpClient 配置好的 OkHttpClient
     * @return 配置好的 Retrofit 實例
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)  // API 的基礎 URL
            .client(okHttpClient)           // 使用配置好的 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 轉換 JSON
            .build()
    }
}

