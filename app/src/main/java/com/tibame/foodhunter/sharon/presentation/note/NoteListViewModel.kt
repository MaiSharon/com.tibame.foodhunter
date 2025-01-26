package com.tibame.foodhunter.sharon.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.core.domain.util.DataError
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.tibame.foodhunter.core.domain.util.Result
import com.tibame.foodhunter.sharon.presentation.NoteListNavigationEvent
import com.tibame.foodhunter.sharon.presentation.util.UiText
import com.tibame.foodhunter.sharon.presentation.util.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {


    private fun startLoading() {
        _state.update {
            it.copy(
                isLoading = true,
                )
        }
    }

    private fun handleSuccess(notes: List<Note>) {
        _state.update {
            it.copy(
                isLoading = false,
                notes = notes)
        }
    }

    private fun handleError(errorMessage: DataError.Network):UiText {
        _state.update {
            it.copy(
                isLoading = false,
                errorMessage = errorMessage.asUiText()
                )
        }
        return errorMessage.asUiText()
    }


    private val _events = Channel<NoteEvent>()
    val events = _events.receiveAsFlow()

    private val _navigationEvents = Channel<NoteListNavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()

    // 控制中心 儲存目前狀態 接收使用者操作請求、處理、更新
    private val _state = MutableStateFlow(NoteState())
    val state = _state
        .onStart { loadNotes() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            NoteState()
        )


    // 用戶做了什麼
    fun onAction(action: NotesListAction) {
        when(action){
            is NotesListAction.OnCreateClick -> {
//                navigateTo(NavigationEvent.ToNoteDetail(null))
            }
            is NotesListAction.OnDeleteClick -> TODO()
            is NotesListAction.OnNoteClick -> {
                navigateTo(NoteListNavigationEvent.ToNoteDetail(action.noteId))
            }
            is NotesListAction.OnBackClick -> {
                navigateTo(NoteListNavigationEvent.Back)

            }
        }
    }

    private fun navigateTo(event: NoteListNavigationEvent) {
        viewModelScope.launch {
            _navigationEvents.send(event)
        }
    }


    private fun loadNotes() {
        viewModelScope.launch {
            startLoading()
            when(val result = repository.getNotes()) {
                is Result.Error -> {
                    val errorMessage = handleError(result.error)
                    _events.send(NoteEvent.ShowError(errorMessage))
                }
                is Result.Success -> handleSuccess(result.data)
            }
        }
    }
}

