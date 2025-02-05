package com.tibame.foodhunter.core.domain.util;

sealed interface NoteError: Error {
    data class TitleTooLong(val maxLength: Int): NoteError
    object TitleEmpty: NoteError
    data class ContentTooLong(val maxLength: Int): NoteError
}
