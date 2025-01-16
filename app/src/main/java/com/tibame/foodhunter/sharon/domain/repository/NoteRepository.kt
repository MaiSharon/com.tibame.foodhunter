package com.tibame.foodhunter.sharon.domain.repository

import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.core.domain.util.DataError
import com.tibame.foodhunter.core.domain.util.Result

interface NoteRepository {

    suspend fun getNotes(): Result<List<Note>, DataError.Network>
}