package com.tibame.foodhunter.sharon.domain.repository

import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.error.DataError
import com.tibame.foodhunter.sharon.domain.error.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NoteRepository {
//    val notes: StateFlow<List<Note>>

    suspend fun getNotes(memberId: String): Result<List<Note>, DataError.Network>
}