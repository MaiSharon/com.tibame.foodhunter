package com.tibame.foodhunter.sharon.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.tibame.foodhunter.sharon.domain.error.Result
import com.tibame.foodhunter.sharon.presentation.util.UiText
import com.tibame.foodhunter.sharon.presentation.util.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteVM @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    // 1. 我的畫畫上需要顯示什麼
    data class NotesState(
        val notes: List<Note> = emptyList(),
        val isLoading: Boolean = false,
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

    //2 .使用者意圖
    sealed class NotesIntent {
        data object LoadNotes : NotesIntent()
        data class AddNote(val note: Note) : NotesIntent()
        data class EditNote(val note: Note) : NotesIntent()
        data class DeleteNote(val noteId: Int) : NotesIntent()
    }

    // 3. 控制中心 儲存目前狀態 接收使用者操作請求、處理、更新
    private val _state = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state.asStateFlow()

    private val eventChannel = Channel<NoteEvent>()
    val events = eventChannel.receiveAsFlow()

    private var memberId: Int? = null

    fun setMemberId(memberId: Int) {
        this.memberId = memberId
        loadNotes(memberId)
    }

    //4. 實作每個操作 可能會發生什麼錯誤？完成後如何更新畫面？
    fun handleIntent(intent: NotesIntent) {
        when(intent){
            NotesIntent.LoadNotes -> loadNotes(memberId!!)
            is NotesIntent.AddNote -> TODO()
            is NotesIntent.DeleteNote -> TODO()
            is NotesIntent.EditNote -> TODO()
        }
    }



    private fun loadNotes(memberId:Int) {
        viewModelScope.launch {
            startLoading()
            when(val result = repository.getNotes(memberId = memberId)) {
                is Result.Error -> {
                    val errorMessage = result.error.asUiText()
                    eventChannel.send(NoteEvent.Error(errorMessage))
                }
                is Result.Success -> handleSuccess(result.data)
            }
        }
    }
}

sealed interface NoteEvent {
    data class Error(val error: UiText): NoteEvent
}