package com.tibame.foodhunter.sharon.data.datasource.base

import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import com.tibame.foodhunter.sharon.domain.error.DataError
import com.tibame.foodhunter.sharon.domain.error.Result

// 定義數據來源的標準介面
interface NoteDataSource{
    suspend fun getNotes(memberId: String):Result<List<NoteDto>, DataError>
}