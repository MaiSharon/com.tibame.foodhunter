package com.tibame.foodhunter.sharon.domain.error

typealias RootError = Error // 類型別名對應Error的接口

sealed interface Result<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): Result<D, E>
    data class Error<out D, out E: RootError>(val error: E): Result<D, E>
}