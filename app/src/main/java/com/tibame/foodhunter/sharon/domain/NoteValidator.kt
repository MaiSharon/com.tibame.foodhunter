package com.tibame.foodhunter.sharon.domain

import com.tibame.foodhunter.core.domain.util.NoteError
import com.tibame.foodhunter.core.domain.util.Result


class NoteValidator {

    fun validateTitle(title: String): Result<Unit, NoteError> {
        if(title.isEmpty()) {
            return Result.Error(NoteError.TitleEmpty)
        }

        if(title.length > MAX_TITLE_LENGTH) {
            return Result.Error(NoteError.TitleTooLong(MAX_TITLE_LENGTH))
        }

        return Result.Success(Unit)
    }

    // 驗證內容
    fun validateContent(content: String): Result<Unit, NoteError> {
        if(content.length > MAX_CONTENT_LENGTH) {
            return Result.Error(NoteError.ContentTooLong(MAX_CONTENT_LENGTH))
        }

        return Result.Success(Unit)
    }

    companion object {
        const val MAX_TITLE_LENGTH = 15
        const val MAX_CONTENT_LENGTH = 500
    }
}