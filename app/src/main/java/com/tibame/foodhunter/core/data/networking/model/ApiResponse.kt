package com.tibame.foodhunter.core.data.networking.model

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T
)