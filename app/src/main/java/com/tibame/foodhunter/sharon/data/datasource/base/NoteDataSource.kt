package com.tibame.foodhunter.sharon.data.datasource.base

import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteResponse

// 定義數據來源的標準介面
interface NoteDataSource {
    // 直接使用 NoteResponse，因為我們會用 Mapper 來處理轉換
    suspend fun getNotes(): List<NoteResponse>
    suspend fun saveNote(note: NoteResponse)
}