package com.tibame.foodhunter.sharon.data

import androidx.lifecycle.ViewModel
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NoteVM @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {


    val notes: StateFlow<List<Note>> = noteRepository.notes
}