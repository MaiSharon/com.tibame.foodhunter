package com.tibame.foodhunter.sharon.internal.util

import com.tibame.foodhunter.BuildConfig

object AppConfig{
    val isTestMode = BuildConfig.DEBUG

    val apiUrl = BuildConfig.BASE_URL
}