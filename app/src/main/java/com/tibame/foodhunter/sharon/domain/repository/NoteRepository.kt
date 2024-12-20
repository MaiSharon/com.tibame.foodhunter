package com.tibame.foodhunter.sharon.domain.repository

import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.error.DataError
import com.tibame.foodhunter.sharon.domain.error.Result

interface NoteRepository {
//    val notes: StateFlow<List<Note>>

    suspend fun getNotes(memberId: Int): Result<List<Note>, DataError.Network>
}