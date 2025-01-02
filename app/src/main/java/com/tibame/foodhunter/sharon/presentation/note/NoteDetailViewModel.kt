package com.tibame.foodhunter.sharon.presentation.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import com.tibame.foodhunter.sharon.presentation.note.NoteDetailViewModel.NoteConstraints.MAX_CONTENT_LENGTH
import com.tibame.foodhunter.sharon.presentation.note.NoteDetailViewModel.NoteConstraints.MAX_TITLE_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject






@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: NoteRepository
):ViewModel()  {
    sealed class NoteDetailAction {
        data class OnTitleChange(val title: String) : NoteDetailAction()
        data class OnContentChange(val content: String) : NoteDetailAction()

        object OnDeleteClick : NoteDetailAction()
        object OnBackAndSaveClick : NoteDetailAction()
        object OnCancelClick : NoteDetailAction()
    }




    sealed interface NoteDetailEvent {
        // 導航事件
        object NavigateBack : NoteDetailEvent
        // 放棄更改事件
        object DiscardChanges : NoteDetailEvent
        // 錯誤訊息
        sealed class Error : NoteDetailEvent {
            object EmptyTitle : Error()
            data class SaveFailed(val reason: String) : Error()
            data class DeleteFailed(val reason: String) : Error()
        }

    }

    private val _state = MutableStateFlow(NoteDetailState(mode = NoteDetailState.NoteMode.Create))
    val state = _state.asStateFlow()

    private val _events = Channel<NoteDetailEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: NoteDetailAction) {
        when (action) {
            NoteDetailAction.OnCancelClick -> handleCancelClick()
            is NoteDetailAction.OnTitleChange -> handleTitleChange(action.title)
            is NoteDetailAction.OnContentChange -> handleContentChange(action.content)
            NoteDetailAction.OnBackAndSaveClick -> TODO()
            NoteDetailAction.OnDeleteClick -> TODO()
        }
    }

//    private fun handleDeleteClick() {
//        val noteId = state.value.
//        repository...getRemove(noteId)
//    }

    private fun handleCancelClick() {
        val currentState = state.value
        when (currentState.mode) {
            is NoteDetailState.NoteMode.Create -> {
                viewModelScope.launch {
                    _events.send(NoteDetailEvent.DiscardChanges)
                }
            }
            is NoteDetailState.NoteMode.Edit -> { }
        }
    }

    private object NoteConstraints {
        const val MAX_TITLE_LENGTH = 15
        const val MAX_CONTENT_LENGTH = 500
    }

    private fun handleContentChange(newContent: String) {
        if (newContent.length <= MAX_CONTENT_LENGTH) {
        _state.update { currentState ->
            currentState.copy(
                input = currentState.input.copy(content = newContent)
                )
            }
        }
    }



    private fun canSaveNote(input: NoteInput): Boolean {
        return input.title.isNotEmpty() && input.title.length <= MAX_TITLE_LENGTH
    }

    private fun handleTitleChange(newTitle: String) {
        _state.update { currentState ->
            val updatedInput = currentState.input.copy(title = newTitle)
            val canSave = canSaveNote(updatedInput)

            currentState.copy(
                input = updatedInput,
                uiControl = currentState.uiControl.copy(
                    showCancelButton = currentState.mode is NoteDetailState.NoteMode.Create,
                    showSaveButton = canSave,
                    showDeleteButton = currentState.mode is NoteDetailState.NoteMode.Edit
                )
            )
        }
    }

    // 業務規則：處理儲存返回邏輯
//    fun handleOnBackAndSaveClick(state: NoteDetailState):NoteDetailEvent {
//        //TODO()
//    }
}
