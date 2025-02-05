package com.tibame.foodhunter.sharon.presentation.note

import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.presentation.util.UiText
import java.util.Date

data class NoteDetailState(
    val mode: NoteMode,         // 使用sealed class區分業務邏輯
    val input: NoteInput = NoteInput(),    // 共享的輸入狀態
    val uiControl: UiControl = UiControl(),       // 共享的UI狀態
    val titleError: UiText? = null,
    val contentError: UiText? = null,
    val isLoading: Boolean = false,         // Detail 頁面自己的 loading 狀態
) {
    sealed interface NoteMode {
        object Add : NoteMode
        data class Edit(val noteId: Int) : NoteMode
    }

    val isValid: Boolean
        get() = titleError == null &&
                contentError == null
}

data class NoteInput(
    val title: String = "",
    val content: String = "",
    val selectedDate: Date = Date()
)

// UI 控制狀態
data class UiControl(
    val showCancelButton: Boolean = false,
    val showSaveButton: Boolean = false,
    val showDeleteButton: Boolean = false,
)

// 我的畫畫上需要顯示什麼
data class NoteState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
)