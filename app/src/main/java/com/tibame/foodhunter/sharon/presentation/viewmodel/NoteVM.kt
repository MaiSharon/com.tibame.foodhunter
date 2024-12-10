package com.tibame.foodhunter.sharon.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tibame.foodhunter.sharon.domain.entity.CardContentType
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.data.NoteRepository
import com.tibame.foodhunter.sharon.domain.entity.event.NoteEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class NoteVM : ViewModel() {
    companion object {
        private const val TAG = "NoteVM"
        private const val SEARCH_DEBOUNCE_TIME = 300L  // 搜尋延遲時間
    }

    // 用於與後端通訊
    private val repository = NoteRepository.instance
    val notes: StateFlow<List<Note>> = repository.notes

//    // 單一筆記狀態 - 用於顯示單一筆記的詳細資訊
//    private val _note = MutableStateFlow<Note?>(null)
//    val note: StateFlow<Note?> = _note.asStateFlow()

    // 所有筆記的完整列表
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())

    // 經過過濾後的筆記列表（這個會顯示給用戶看）
    private val _filteredNotes = MutableStateFlow<List<Note>>(emptyList())
    val filteredNotes: StateFlow<List<Note>> = _filteredNotes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")


    private var memberId: Int? = null

    fun setMemberId(memberId: Int) {
        this.memberId = memberId
        loadNotes(memberId)
    }

    // 預留篩選相關狀態，目前未實作
    // private val _selectedContentTypes = MutableStateFlow<Set<CardContentType>>(emptySet())
    // val selectedContentTypes = _selectedContentTypes.asStateFlow()

    init {
        viewModelScope.launch {
            NoteEvent.refreshTrigger.collect { needRefresh ->
                if (needRefresh && memberId != null) {
                    loadNotes(memberId!!)
                    NoteEvent.resetTrigger()  // 重置觸發器
                }
            }
        }
        setupSearchFlow()  // 搜尋資料流
    }

    /**
     * 初始化筆記列表
     */
    fun loadNotes(memberId: Int) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "開始載入筆記")
                _isLoading.value = true

                Log.d(TAG, "呼叫 repository.getNotes()")
                repository.getNotes(memberId)

                // 只收集一次資料
                val notes = repository.notes.first()
                _allNotes.value = notes
                _filteredNotes.value = notes
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing notes", e)
            } finally {
                Log.d(TAG, "載入完成，設置 isLoading = false")
                _isLoading.value = false
            }
        }
    }

    /**
     * 1. 搜尋流程設置
     * 使用 Flow 處理搜尋邏輯，包含：
     * 1.1. 防抖（debounce）：避免過於頻繁的搜尋
     * 1.2. 即時過濾：搜尋條件改變時自動更新結果
     *  這裡使用 Flow 的特性自動處理狀態更新
     */
    @OptIn(FlowPreview::class)
    private fun setupSearchFlow() {
        viewModelScope.launch {
            _searchQuery
                .debounce(SEARCH_DEBOUNCE_TIME)  // 防抖，避免頻繁搜尋
                .collect { query ->
                    Log.d(TAG, "[setupSearchFlow] 搜尋關鍵字更新: $query")
                    updateSearchResults(query)
                }
        }
    }

    /**
     * 2. 接收搜尋請求
     */
    fun searchNotes(query: String) {
        Log.d(TAG, "[searchNotes] 接收搜尋請求: $query")
        viewModelScope.launch {
            _searchQuery.value = query
            Log.d(TAG, "[searchNotes] 更新搜尋狀態")
        }
    }

    /**
     * 3. 搜尋結果更新
     */
    private fun updateSearchResults(query: String) {
        Log.d(TAG, "[updateSearchResults] 開始更新搜尋結果")

        val allNotes = _allNotes.value

        // 套用搜尋文字
        val result = if (query.isEmpty()) {
            allNotes
        } else {
            allNotes.filter { note ->
                note.title.contains(query, ignoreCase = true)
            }
        }

        Log.d(TAG, "[updateSearchResults] 過濾後結果數量: ${result.size}")
        _filteredNotes.value = result
        Log.d(TAG, "_filteredNotes value: ${_filteredNotes.value}")
    }


    val hasSearchQuery: Boolean
        get() = _searchQuery.value.isNotEmpty()

    /**
     * 預留篩選方法，目前未實作
     */
    fun handleFilter(filter: CardContentType) {
        // 預留給未來實作篩選功能
    }

    /**
     * 預留重置篩選方法，目前未實作
     */
    fun resetFilters() {
        // 預留給未來實作篩選功能
    }


    /**
     * 資源清理
     */
    override fun onCleared() {
        super.onCleared()
        // 清理資源
    }
}