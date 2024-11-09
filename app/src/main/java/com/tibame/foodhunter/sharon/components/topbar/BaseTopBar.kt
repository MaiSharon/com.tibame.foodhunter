package com.tibame.foodhunter.sharon.components.topbar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.components.SearchBar
import com.tibame.foodhunter.sharon.data.CardContentType
import com.tibame.foodhunter.sharon.viewmodel.CalendarVM
import com.tibame.foodhunter.sharon.viewmodel.PersonalToolsVM
import com.tibame.foodhunter.ui.theme.FColor


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopBarPreview(){
    val mockNavController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

//    BaseTopBar(mockNavController,scrollBehavior)
//    CalendarTopBar(mockNavController,scrollBehavior, PersonalToolsVM(noteVM, calendarVM), CalendarVM())
//    NoteTopBar(mockNavController,scrollBehavior, PersonalToolsVM())

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    isSearchVisible: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onToggleSearchVisibility: () -> Unit,
    showFilter: Boolean,
    onFilter: () -> Unit = {},
) {
    TopAppBar(
        title = {
            if (isSearchVisible) {
                // 當搜尋狀態時，使用文字輸入框作為標題
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholder = { Text("搜尋", color = FColor.Gary, fontSize = 16.sp) },
                    active = isSearchVisible,
                    onActiveChange = { onToggleSearchVisibility() },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                // 正常情況下的標題
                Text(stringResource(R.string.str_member))
            }
        },
        navigationIcon = {
            if (isSearchVisible) {
                // 搜尋狀態下顯示的返回按鈕，用於取消搜尋
                IconButton(onClick = { onToggleSearchVisibility() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "取消搜尋"
                    )
                }
            } else {
                // 正常狀態下顯示的返回按鈕
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "返回"
                    )
                }
            }
        },
        actions = {
            if (!isSearchVisible) {
                // 非搜尋狀態、允許顯示篩選時，顯示搜尋按鈕和篩選按鈕
                IconButton(onClick = onToggleSearchVisibility) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                if(showFilter) {

                    IconButton(onClick = onFilter) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Filter")
                    }
                }

            } else {
                // 搜尋狀態允許顯示篩選時，顯示篩選按鈕
                if (showFilter) {
                    IconButton(onClick = onFilter) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Filter")
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = FColor.Dark_80,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    personalToolsVM: PersonalToolsVM,
    calendarVM: CalendarVM
) {
    val topBarState by personalToolsVM.topBarState.collectAsState()


    Column{
        BaseTopBar(
            navController = navController,
            scrollBehavior = scrollBehavior,
            // 傳遞搜尋相關參數
            isSearchVisible = topBarState.isSearchVisible,
            searchQuery = topBarState.searchQuery,
            onSearchQueryChange = { newQuery ->
                // 當搜尋文字改變時更新 ViewModel
                personalToolsVM.onSearchQueryChange(newQuery)
            },
            onToggleSearchVisibility = {
                // 切換搜尋框顯示狀態
                personalToolsVM.toggleSearchVisibility()
            },
            showFilter = true,
            onFilter = {
                personalToolsVM.toggleFilterChipVisibility()
            }
        )
        // 新增：顯示篩選 Chip
        if (topBarState.isFilterChipVisible) {
            FilterChipSection(
                selectedFilters = calendarVM.selectedContentTypes.collectAsState().value,
                onFilterSelected = { filter ->
                    calendarVM.handleFilter( filter)
                }
            )
        }
    }
}


@Composable
fun FilterChipSection(
    selectedFilters: Set<CardContentType>,
    onFilterSelected: (CardContentType) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        ),
        color = Color.White,

    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            CardContentType.entries.forEach { filterType ->
                FilterChip(
                    selected = filterType in selectedFilters,
                    onClick = { onFilterSelected(filterType) },
                    label = {
                        Text(
                            when (filterType) {
                                CardContentType.NOTE -> "手札"
                                CardContentType.GROUP -> "揪團"
                            }
                        )
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    personalToolsVM: PersonalToolsVM,
) {
    val topBarState by personalToolsVM.topBarState.collectAsState()

    Column {
        BaseTopBar(
            navController = navController,
            scrollBehavior = scrollBehavior,
            isSearchVisible = topBarState.isSearchVisible,
            searchQuery = topBarState.searchQuery,
            onSearchQueryChange = { newQuery ->
                Log.d("NoteTopBar", "搜尋輸入: $newQuery")
                personalToolsVM.onSearchQueryChange(newQuery) },
            onToggleSearchVisibility = {
                Log.d("NoteTopBar", "切換搜尋欄位顯示")
                personalToolsVM.toggleSearchVisibility() },
            showFilter = false,
        )

//        if (isFilterChipVisible) {
//            FilterChipSection(
//                selectedFilters = selectedFilters,
//                onFilterSelected = { filter ->
//                    noteViewModel.updateSelectedFilter(filter)
//                }
//            )
//        }
    }
}