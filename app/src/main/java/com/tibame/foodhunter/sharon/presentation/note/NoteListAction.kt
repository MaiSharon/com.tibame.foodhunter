package com.tibame.foodhunter.sharon.presentation.note

sealed interface NotesListAction {
    object OnCreateClick : NotesListAction
    data class OnNoteClick(val noteId: Int) : NotesListAction
    data class OnDeleteClick(val noteId: Int) : NotesListAction
    object OnBackClick : NotesListAction
}