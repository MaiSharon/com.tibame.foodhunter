package com.tibame.foodhunter.sharon.domain.repository

import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.data.datasource.remote.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface INoteRepository {
    val notes: StateFlow<List<Note>>

    suspend fun getNotes(memberId: String): Flow<Result<List<Note>>> // Response的庫要使用retrofit2
}