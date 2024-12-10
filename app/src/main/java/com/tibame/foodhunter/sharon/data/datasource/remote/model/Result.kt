package com.tibame.foodhunter.sharon.data.datasource.remote.model


sealed class Result<out T>{
    data class Success<T>(
        val data: T,
        val message: String
        ) : Result<T>()

    data class Error(
        val code: Int,
        val message: String
    ) : Result<Nothing>()

    object Loading: Result<Nothing>()
}