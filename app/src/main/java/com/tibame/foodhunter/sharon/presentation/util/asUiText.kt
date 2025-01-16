package com.tibame.foodhunter.sharon.presentation.util

import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.domain.util.DataError


fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.the_request_timed_out
        )

        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.youve_hit_your_rate_limit
        )

        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.no_internet
        )

        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.file_too_large
        )

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.server_error
        )

        DataError.Network.PARSE_ERROR -> UiText.StringResource(
            R.string.error_parse_error
        )

        DataError.Network.DOMAIN_CONVERSION_ERROR -> UiText.StringResource(
            R.string.domain_conversion_error
        )

        DataError.Network.UNKNOWN -> UiText.StringResource(
            R.string.unknown_error
        )

        DataError.Local.DISK_FULL -> UiText.StringResource(
            R.string.error_disk_full
        )

        DataError.Network.BAD_REQUEST -> UiText.StringResource(
            R.string.bad_request_error
        )
        DataError.Network.NOT_FOUND -> UiText.StringResource(
            R.string.not_found_error
        )
    }
}

//fun Result.Error<*, DataError>.asErrorUiText(): UiText {
//    return error.asUiText()
//}