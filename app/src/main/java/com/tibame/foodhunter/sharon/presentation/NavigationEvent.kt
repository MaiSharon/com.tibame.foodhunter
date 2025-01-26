package com.tibame.foodhunter.sharon.presentation



sealed interface NoteListNavigationEvent {
    object Back : NoteListNavigationEvent
    data class ToNoteDetail(val noteId: Int) : NoteListNavigationEvent
}

sealed interface NoteDetailNavigationEvent {
    object Back : NoteDetailNavigationEvent
}