package com.tibame.foodhunter.sharon.presentation

import androidx.navigation.NavController

object Routes {
    const val PERSONAL_TOOLS = "personal_tools"
    const val NOTE_DETAIL = "note_detail"

    // 建立帶參數的路由
    fun noteDetail(noteId: Int?) = "$NOTE_DETAIL?noteId=$noteId"
    fun addNote() = NOTE_DETAIL
}