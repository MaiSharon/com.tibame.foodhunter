package com.tibame.foodhunter.sharon.presentation.note;

sealed interface NoteDetailAction {
    data class OnTitleChange(val title: String) : NoteDetailAction
    data class OnContentChange(val content: String) : NoteDetailAction

    object OnDeleteClick: NoteDetailAction
    object OnBackAndSaveClick : NoteDetailAction
    object OnCancelClick : NoteDetailAction
}
