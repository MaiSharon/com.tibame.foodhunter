package com.tibame.foodhunter.sharon.presentation.viewmodel//package com.tibame.foodhunter.sharon.viewmodel
//
//import androidx.lifecycle.ViewModel
//import com.tibame.foodhunter.sharon.TabConstants
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//open class TopBarViewModel : ViewModel() {
//    // 保存當前選中的 Tab，默認為 0（例如 Calendar）
//    private val _selectedTab = MutableStateFlow(TabConstants.CALENDAR)
//    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()
//
//    // 更新選中的 Tab
//    fun updateSelectedTab(index: Int) {
//        _selectedTab.value = index
//    }
//
//    // 控制搜尋框顯示的狀態
//    protected val _isSearchVisible = MutableStateFlow(false)
//    val isSearchVisible: StateFlow<Boolean> = _isSearchVisible.asStateFlow()
//
//    // 搜尋關鍵字
//    protected val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
//
//    // 控制篩選 Chip 的顯示狀態（新增）
//    protected val _isFilterChipVisible = MutableStateFlow(false)
//    val isFilterChipVisible: StateFlow<Boolean> = _isFilterChipVisible.asStateFlow()
//
//    // 切換搜尋框的顯示狀態（顯示或隱藏）
//    open fun toggleSearchVisibility() {
//        _isSearchVisible.value = !_isSearchVisible.value
//        // 如果關閉搜尋，關閉篩選 Chip 並清除搜尋結果（新增）
//        if (!_isSearchVisible.value) {
//            _searchQuery.value = ""
//            _isFilterChipVisible.value = false
//        }
//    }
//
//    // 更新搜尋關鍵字
//    open fun onSearchQueryChange(newQuery: String) {
//        _searchQuery.value = newQuery
//    }
//
//    // 切換篩選 Chip 的顯示狀態（新增）
//    open fun toggleFilterChipVisibility() {
//        _isFilterChipVisible.value = !_isFilterChipVisible.value
//    }
//}
