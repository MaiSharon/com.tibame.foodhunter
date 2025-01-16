package com.tibame.foodhunter.core.domain.util

sealed interface DataError : Error {
    enum class Network: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        PARSE_ERROR,
        DOMAIN_CONVERSION_ERROR,
        BAD_REQUEST,
        NOT_FOUND,
        UNKNOWN
    }
    enum class Local: DataError {
        DISK_FULL
    }
}