package com.tibame.foodhunter.sharon.presentation.note

import com.tibame.foodhunter.sharon.presentation.util.UiText

sealed interface NoteEvent {
    data class Error(val error: UiText): NoteEvent
}