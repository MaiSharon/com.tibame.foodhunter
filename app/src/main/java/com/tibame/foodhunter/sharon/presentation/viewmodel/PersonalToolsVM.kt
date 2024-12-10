package com.tibame.foodhunter.sharon.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.presentation.ui.constant.TabConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Personal Tools 的 UI 狀態
 */
data class PersonalToolsUiState(
    var selectedTabIndex: Int = TabConstants.CALENDAR,

    // 顯示tab名稱用
    val tabList: List<Int> = listOf(
        R.string.str_calendar,
        R.string.str_note
    )
)

/**
 * TopBar 狀態
 */
data class TopBarState(
    val isSearchVisible: Boolean = false,    // 搜尋框是否顯示
    val searchQuery: String = "",            // 保存ui上的搜尋文字
    val isFilterChipVisible: Boolean = false // 篩選chips是否顯示
)

class PersonalToolsVM: ViewModel() {
    companion object {
        private const val TAG = "PersonalToolsVM"
    }

    // UI 狀態
    private val _uiState = MutableStateFlow(PersonalToolsUiState())
    val uiState = _uiState.asStateFlow()

    // TopBar 狀態
    private val _topBarState = MutableStateFlow(TopBarState())
    val topBarState = _topBarState.asStateFlow()

    private val calendarVM = CalendarVM()
    val calendarState = this.calendarVM.uiState

    fun goToCalendarTab(){
        updateSelectedTab(TabConstants.CALENDAR)
    }

    fun goToNoteTab(){
        updateSelectedTab(TabConstants.NOTE)
    }

    /**
     * 切換Tab時重置TopBar狀態
     */
    fun updateSelectedTab(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
        // 切換Tab時重置TopBar狀態
        resetTopBarState()
    }

    /**
     * 重置TopBar狀態
     */
    private fun resetTopBarState() {
        _topBarState.value = TopBarState()
        _noteSearchQuery.value = ""
        _calendarSearchQuery.value = ""
    }

    fun toggleSearchVisibility() {
        viewModelScope.launch {
            val willClose = _topBarState.value.isSearchVisible
            Log.d(TAG, "切換搜尋框顯示狀態，當前是否顯示: $willClose")

            if (willClose) {
                // 1. 先發送搜尋事件
                when (_uiState.value.selectedTabIndex) {
                    TabConstants.NOTE -> {
                        _noteSearchQuery.value = ""
                        Log.d(TAG, "已發送清空事件到 Note")
                    }
                    TabConstants.CALENDAR -> {
                        _calendarSearchQuery.value = ""
                        Log.d(TAG, "已發送清空事件到 Calendar")
                    }
                }
            }

            // 2. 再更新 UI 狀態
            _topBarState.update { currentState ->
                currentState.copy(
                    isSearchVisible = !currentState.isSearchVisible,
                    searchQuery = if (willClose) "" else currentState.searchQuery
                )
            }
            Log.d(TAG, "搜尋框狀態已更新: ${if (!willClose) "顯示" else "隱藏"}")
        }
    }

    /**
     * 搜尋數據流 - 分別給不同的 Tab
     */
    private val _calendarSearchQuery = MutableStateFlow("")
    val calendarSearchQuery = _calendarSearchQuery.asStateFlow()

    private val _noteSearchQuery = MutableStateFlow("")
    val noteSearchQuery = _noteSearchQuery.asStateFlow()

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            Log.d(TAG, "[onSearchQueryChange] 搜尋文字更新: $query")

            // 1. 更新 UI 狀態
            _topBarState.update { it.copy(searchQuery = query) }

            // 2. 根據當前 Tab 發送事件
            when (_uiState.value.selectedTabIndex) {
                TabConstants.CALENDAR -> {
                    _calendarSearchQuery.emit(query)
                    Log.d(TAG, "發送搜尋事件到 Calendar: $query")
                }
                TabConstants.NOTE -> {
                    _noteSearchQuery.emit(query)
                    Log.d(TAG, "發送搜尋事件到 Note: $query")
                }
            }
        }
    }

    /**
     * 篩選器顯示狀態
     */
    fun toggleFilterChipVisibility() {
        _topBarState.update { currentState ->
            currentState.copy(
                isFilterChipVisible = !currentState.isFilterChipVisible,
            )
        }

        // 如果是關閉篩選，通知當前頁面重置篩選狀態
        if (!_topBarState.value.isFilterChipVisible) {
            when (_uiState.value.selectedTabIndex) {
                TabConstants.CALENDAR -> calendarVM.resetFilters()
            }
        }
    }
}