package com.tibame.foodhunter.sharon.presentation.note

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.error.DataError
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.tibame.foodhunter.sharon.domain.error.Result
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
class NoteVM @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    sealed class NoteNavigationEvent {
        data class ToNoteDetail(val noteId: Int?) : NoteNavigationEvent()
        object Back : NoteNavigationEvent()
    }

    // 1. 我的畫畫上需要顯示什麼
    @Immutable
    data class NotesState(
        val notes: List<Note> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: UiText? = null
    )

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

    //2 .使用者意圖
    sealed class NotesListAction {
        object OnCreateClick : NotesListAction()
        data class OnNoteClick(val noteId: Int) : NotesListAction()
        data class OnDeleteClick(val noteId: Int) : NotesListAction()
    }

    private var memberId: Int? = null

    fun setMemberId(memberId: Int) {
        this.memberId = memberId
        loadNotes(memberId)
    }

    // 3. 控制中心 儲存目前狀態 接收使用者操作請求、處理、更新
    private val _state = MutableStateFlow(NotesState())
    val state = _state
        .onStart { loadNotes(memberId!!) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            NotesState()
        )

    private val _events = Channel<NoteEvent>()
    val events = _events.receiveAsFlow()

    private val _navigationEvents = Channel<NoteNavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()


    //4. 用戶做了什麼
    fun onAction(action: NotesListAction) {
        when(action){
            NotesListAction.OnCreateClick -> TODO()
            is NotesListAction.OnDeleteClick -> TODO()
            is NotesListAction.OnNoteClick -> {
                viewModelScope.launch {
                    _navigationEvents.send(
                        NoteNavigationEvent.ToNoteDetail(action.noteId)
                    )
                }
            }
        }
    }



    private fun loadNotes(memberId:Int) {
        viewModelScope.launch {
            startLoading()
            when(val result = repository.getNotes(memberId = memberId)) {
                is Result.Error -> {
                    val errorMessage = handleError(result.error)
                    _events.send(NoteEvent.Error(errorMessage))
                }
                is Result.Success -> handleSuccess(result.data)
            }
        }
    }
}

sealed interface NoteEvent {
    data class Error(val error: UiText): NoteEvent
}