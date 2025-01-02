package com.tibame.foodhunter.sharon.presentation.note

import java.time.LocalDate

data class NoteDetailState(
    val mode: NoteMode,         // 使用sealed class區分業務邏輯
    val input: NoteInput = NoteInput(),    // 共享的輸入狀態
    val uiControl: UiControl = UiControl()       // 共享的UI狀態
) {
    sealed interface NoteMode {
        object Create : NoteMode
        data class Edit(val noteId: Int) : NoteMode
    }
}

// 輸入數據
data class NoteInput(
    val title: String = "",
    val content: String = "",
    val selectedDate: LocalDate = LocalDate.now()
)

// UI 控制狀態
data class UiControl(
    val showCancelButton: Boolean = false,
    val showSaveButton: Boolean = false,
    val showDeleteButton: Boolean = false,
    val isLoading: Boolean = false
)