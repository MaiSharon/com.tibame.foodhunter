package com.tibame.foodhunter.sharon.util

import com.tibame.foodhunter.BuildConfig

object AppConfig{
    val isTestMode = BuildConfig.DEBUG

    val apiUrl = BuildConfig.API_URL
}