package com.tibame.foodhunter.sharon.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class NoteVM (
    private val noteRepository: INoteRepository
) : ViewModel() {
  // repository 的數據用stateFlow放在這
    val notes: StateFlow<List<Note>> = noteRepository.notes
}