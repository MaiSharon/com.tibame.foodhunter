package com.tibame.foodhunter.sharon.data.datasource.base

import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import com.tibame.foodhunter.core.domain.util.DataError
import com.tibame.foodhunter.core.domain.util.Result

// 定義數據來源的標準介面
interface NoteDataSource{
    suspend fun getNotes(): Result<List<NoteDto>, DataError>

}