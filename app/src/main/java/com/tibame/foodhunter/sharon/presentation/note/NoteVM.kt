package com.tibame.foodhunter.sharon.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import com.tibame.foodhunter.sharon.domain.error.Result
import com.tibame.foodhunter.sharon.presentation.util.asUiText
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


    //4. 實作每個操作 可能會發生什麼錯誤？完成後如何更新畫面？
    fun handleIntent(intent: NotesIntent) {
        when(intent){
            NotesIntent.LoadNotes -> loadNotes()
            is NotesIntent.AddNote -> TODO()
            is NotesIntent.DeleteNote -> TODO()
            is NotesIntent.EditNote -> TODO()
        }
    }


    private fun loadNotes() {
        viewModelScope.launch {

            when(val result = repository.getNotes(memberId = "1")) {
                is Result.Error -> {
                    result.error.asUiText()
                }
                is Result.Success -> {
                    _state.update {
                        it.copy(notes = result.data)
                    }
                }
            }



        }
    }


}