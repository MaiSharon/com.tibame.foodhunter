package com.tibame.foodhunter.sharon.domain.repository

import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.error.DataError
import com.tibame.foodhunter.sharon.domain.error.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NoteRepository {
    val notes: StateFlow<List<Note>>

    suspend fun getNotes(memberId: String): Flow<Result<List<Note>, DataError.Network>> // Response的庫要使用retrofit2
}