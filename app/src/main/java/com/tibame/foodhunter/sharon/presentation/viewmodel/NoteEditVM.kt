package com.tibame.foodhunter.sharon.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.tibame.foodhunter.sharon.domain.entity.CardContentType
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.data.NoteRepository
import com.tibame.foodhunter.sharon.domain.entity.event.NoteEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 筆記編輯頁面的 UI 狀態
 * 包含頁面上所有需要追蹤的狀態
 */
data class NoteEditUiState(
    val isFirstEntry: Boolean = true,  // 是否首次進入
    val hasTitle: Boolean = false,     // 是否有標題
    val isExistingNote: Boolean = false, // 是否為既有筆記
    val title: String = "",               // 標題
    val content: String = "",             // 內容
    val restaurantId: Int = 1,  // 先給固定值
    val type: CardContentType = CardContentType.NOTE, // 筆記類型
    val isLoading: Boolean = false,       // 加載狀態
    val errorMessage: String? = null,      // 錯誤訊息
    val selectedDate: Date = Date(),  // 給 DatePicker 用
    val displayDate: String = "",     // 給卡片顯示用 (MM/dd)
    val displayDay: String = "",      // 給卡片顯示用 (星期幾)
//    val memberId: Int = userVM.memberId.value
)

/**
 * 定義筆記編輯頁面可能發生的所有事件
 */
sealed class NoteEditEvent {
    /**
     * 更新標題事件
     * @param title 新的標題內容
     * 使用時機：
     * 1. 使用者輸入/修改標題時
     * 2. 清空標題時
     */
    data class UpdateTitle(val title: String) : NoteEditEvent()
    data class UpdateContent(val content: String) : NoteEditEvent()
    data class UpdateRestaurant(val name: String?) : NoteEditEvent()
    data class UpdateDate(val date: Date) : NoteEditEvent()

    // 操作相關事件
//    object SaveNote : NoteEditEvent()      // 保存筆記
    object NavigateBack : NoteEditEvent()  // 返回上一頁
    object DismissError : NoteEditEvent()  // 關閉錯誤提示
}


/**
 * 筆記編輯頁面的 ViewModel
 */
class NoteEditVM : ViewModel() {
    companion object {
        private const val TAG = "NoteEditVM"
    }

    // UI 狀態
    private val _uiState = MutableStateFlow(NoteEditUiState())
    val uiState = _uiState.asStateFlow()

    // 當前編輯的筆記
    private val _note = MutableStateFlow<Note?>(null)
    val note = _note.asStateFlow()

    // 初始化 repository
    private val repository = NoteRepository.instance

    // 新增保存成功的事件
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()


    private var memberId: Int? = null

    fun setMemberId(newMemberId: Int) {
        memberId = newMemberId
    }

    /**
     * 載入指定 ID 的筆記
     */
    fun loadNote(noteId: Int) {

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val noteData = repository.getNoteById(noteId)
                _note.value = noteData

                // 更新 UI 狀態
                noteData?.let { note ->
                    _uiState.update { state ->
                        state.copy(
                            isFirstEntry = false,      // 設為非首次進入
                            isExistingNote = true,     // 設為既有筆記
                            hasTitle = note.title.isNotEmpty(),
                            title = note.title,
                            content = note.content,
                            restaurantId = 1,
                            type = note.type,
                            selectedDate = note.selectedDate
                        )
                    }
                    Log.d(TAG, "筆記載入成功: ${note.title}")

                } ?: run {
                    Log.e(TAG, "找不到指定筆記，noteId: $noteId")
                    _uiState.update {
                        it.copy(errorMessage = "找不到指定筆記")
                    }
                    return@launch
                }
            } catch (e: Exception) {
                Log.e(TAG, "載入筆記失敗", e)
                _uiState.update {
                    it.copy(errorMessage = "載入筆記失敗：${e.message}")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * 處理所有 UI 事件
     */
    fun onEvent(event: NoteEditEvent) {
        when (event) {
            is NoteEditEvent.UpdateTitle -> handleTitleUpdate(event.title)

            is NoteEditEvent.UpdateContent -> {
                _uiState.update { it.copy(content = event.content) }
            }

            is NoteEditEvent.UpdateRestaurant -> {
                _uiState.update { it.copy(restaurantId = 1) }
            }

            is NoteEditEvent.UpdateDate -> {
                val dateString = formatDate(event.date)  // MM/dd 格式的日期
                val dayString = formatDay(event.date)    // 星期格式的日期

                _uiState.update {
                    it.copy(
                        selectedDate = event.date,
                        displayDate = dateString,   // 使用格式化後的日期字串
                        displayDay = dayString      // 使用格式化後的星期字串
                    )
                }
            }
            is NoteEditEvent.NavigateBack -> {
                // 導航邏輯會在 UI 層處理
            }

            is NoteEditEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun handleTitleUpdate(newTitle: String) {
        _uiState.update { currentState ->
            currentState.copy(
                title = newTitle,
                hasTitle = newTitle.isNotEmpty(),
                isFirstEntry =
                if (currentState.isFirstEntry && newTitle.isEmpty()) true
                else currentState.isFirstEntry
            )
        }
        // 筆記規則
        _uiState.update { currentState ->
            when {
//                // 情境1: 首次輸入且有標題
//                uiState.value.isFirstEntry && newTitle.isNotEmpty() -> {
//                    Log.d(TAG, "首次輸入標題、標題不為空")
//                }
//
//                // 情境2: 非首次輸入且標題被清空
//                !uiState.value.isFirstEntry && newTitle.isEmpty() -> {
//                    Log.d(TAG, "既有筆記標題被清空，顯示警告")
//                }
//
//                // 情境3: 既有筆記修改
//                !uiState.value.isFirstEntry && uiState.value.isExistingNote && newTitle.isNotEmpty() -> {
//                    Log.d(TAG, "既有筆記標題修改、標題不為空、返回即可更新")
//                }
                else -> {
                    currentState.copy(title = newTitle, hasTitle = newTitle.isNotEmpty())
                }
            }
        }
    }

    /**
     * 返回按鈕處理
     */
    fun saveAndNavigateBack(navController: NavHostController) {
        Log.d(TAG, "開始執行 saveAndNavigateBack")
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                // 檢查是否可以儲存
                val canSave = currentState.hasTitle &&
                        (currentState.isFirstEntry || currentState.isExistingNote)
                if (canSave) {
                    Log.d(TAG, "標題檢查通過，開始保存筆記")

                    val saveResult = saveNote()
                    Log.d(TAG, "保存結果: $saveResult")
                    _uiState.update { it.copy(isLoading = false) }

                    // 檢查保存是否成功
                    if (_saveSuccess.value) {
                        Log.d(TAG, "保存成功，觸發刷新")
                        delay(300)
                    } else {
                        Log.e(TAG, "保存失敗: saveSuccess = ${_saveSuccess.value}")
                    }
                } else {
                    Log.w(TAG, "未輸入標題，無法保存")
                    return@launch  // 不返回上一頁
                }
                NoteEvent.triggerRefresh()
                navController.popBackStack()
            } catch (e: Exception) {
                Log.e(TAG, "saveAndNavigateBack 發生異常", e)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
                Log.e(TAG, "返回刷新")
            }
        }
    }

    /**
     * 筆記保存邏輯
     * 功能：
     * 1. 處理新增/更新筆記
     * 2. 更新筆記狀態
     * 3. 錯誤處理
     */
    private suspend fun saveNote(): Boolean {
        val currentMemberId = memberId
        if (currentMemberId == null) {
            Log.e(TAG, "會員ID未設置，無法保存筆記")
            _uiState.update { it.copy(errorMessage = "會員ID未設置") }
            return false
        }

        Log.d(TAG, "開始執行 saveNote")
        return try {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value
            Log.d(
                TAG, "當前狀態: " +
                        "isExistingNote=${currentState.isExistingNote}, " +
                        "title=${currentState.title}, " +
                        "restaurantId=${currentState.restaurantId}, " +
                        "memberId=$currentMemberId"
            )

            // 檢查標題
            if (currentState.title.isEmpty()) {
                Log.w(TAG, "標題為空，中止保存")
                _uiState.update { it.copy(errorMessage = "請輸入標題") }
                return false
            }

            val result = if (currentState.isExistingNote) {
                Log.d(TAG, "更新既有筆記")
                note.value?.noteId?.let { noteId ->
                    Log.d(TAG, "正在更新筆記 ID: $noteId")
                    repository.updateNote(
                        noteId = noteId,
                        title = currentState.title,
                        content = currentState.content,
                        restaurantId = currentState.restaurantId,
                        selectedDate = currentState.selectedDate
                    )
                } ?: run {
                    Log.e(TAG, "無效的筆記 ID")
                    -1
                }
            } else {
                Log.d(TAG, "新增筆記")
                repository.addNote(
                    title = currentState.title,
                    content = currentState.content,
                    restaurantId = currentState.restaurantId,
                    memberId = memberId!!,
                    selectedDate = currentState.selectedDate
                )
            }

            Log.d(TAG, "資料庫操作結果: $result")

            if (result > 0) {
                _saveSuccess.value = true
                Log.d(TAG, "保存成功")
                true
            } else {
                Log.e(TAG, "保存失敗，result = $result")
                _uiState.update { it.copy(errorMessage = "儲存失敗") }
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "保存過程發生異常", e)
            _uiState.update { it.copy(errorMessage = "保存失敗：${e.message}") }
            false
        } finally {
            _uiState.update { it.copy(isLoading = false) }
            Log.d(TAG, "saveNote 執行結束")
        }
    }

    /**
     * 刪除筆記邏輯
     */
    fun deleteNote(navController: NavHostController) {
        viewModelScope.launch {
            val noteId = note.value?.noteId  // 從當前 note 中取得 ID
            if (noteId != null) {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    val result = repository.deleteNoteById(noteId)

                    if (result > 0) {

                        NoteEvent.triggerRefresh()

                        delay(500)

                        _uiState.update { it.copy(isLoading = false) }

                        navController.popBackStack()
                    } else {
                        Log.e("NoteEditVM", "刪除筆記失敗")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "刪除失敗"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NoteEditVM", "刪除筆記時發生錯誤", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "刪除失敗：${e.message}"
                        )
                    }
                }
            } else {
                Log.e("NoteEditVM", "無效的筆記 ID，無法刪除")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "無效的筆記 ID"
                    )
                }
            }
        }
    }


    /**
     * 格式化日期 (MM/dd)
     */
    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * 格式化星期幾
     */
    private fun formatDay(date: Date): String {
        val dayFormat = SimpleDateFormat("EEEE", Locale.CHINESE)
        return dayFormat.format(date)

    }
}




