package com.tibame.foodhunter.sharon.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.sharon.CalendarWidget
import com.tibame.foodhunter.sharon.components.card.NoteOrGroupCard
import com.tibame.foodhunter.sharon.util.CalendarUiState
import com.tibame.foodhunter.sharon.data.CardContentType
import com.tibame.foodhunter.sharon.data.Group
import com.tibame.foodhunter.sharon.data.Note
import com.tibame.foodhunter.sharon.util.DateUtil
import com.tibame.foodhunter.sharon.viewmodel.CalendarVM
import com.tibame.foodhunter.ui.theme.FColor
import java.time.LocalDate
import java.util.Calendar

@Composable
fun CalendarScreen(
    navController: NavHostController,
    calendarVM: CalendarVM,
    userVM: UserViewModel,
) {
    val uiState by calendarVM.uiState.collectAsState()
    val filteredItems by calendarVM.filteredItems.collectAsStateWithLifecycle()
    val isLoading by calendarVM.isLoading.collectAsStateWithLifecycle()
    val memberId by userVM.memberId.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()

    // 初始化資料 取得用戶 ID 並初始化資料
    LaunchedEffect(memberId) {
        calendarVM.initUserData(memberId)

    }

    // 當筆記列表更新時，自動滾動到頂部
    LaunchedEffect(filteredItems.size) {
        if (filteredItems.isNotEmpty()) {
            lazyListState.animateScrollToItem(0)
        }
    }

    // 選中日期的狀態
    var selectedDate by remember {
        mutableStateOf<CalendarUiState.Date?>(null)
    }

    // 當天要顯示的項目
    var dayItems by remember {
        mutableStateOf<List<Any>>(emptyList())
    }

    // 1. 設置初始日期為當天
    LaunchedEffect(uiState.dates) {
        if (selectedDate == null) {
            val today = LocalDate.now()
            selectedDate = uiState.dates.find { date ->
                date.dayOfMonth == today.dayOfMonth.toString() &&
                        date.month == today.monthValue &&
                        date.year == today.year
            }
        }
    }

    LaunchedEffect(selectedDate, filteredItems) {
        selectedDate?.let { date ->
            dayItems = filteredItems.filter { item ->
                Log.d("CalendarScreen", "filteredItems = empt ${filteredItems.size}")

                when (item) {
                    is Note -> {
                        val calendar = Calendar.getInstance().apply {
                            time = item.selectedDate
                        }
                        calendar.get(Calendar.DAY_OF_MONTH).toString() == date.dayOfMonth &&
                                (calendar.get(Calendar.MONTH) + 1) == date.month &&
                                calendar.get(Calendar.YEAR) == date.year
                    }
                    is Group -> {
                        val calendar = Calendar.getInstance().apply {
                            time = item.groupDate
                        }
                        calendar.get(Calendar.DAY_OF_MONTH).toString() == date.dayOfMonth &&
                                (calendar.get(Calendar.MONTH) + 1) == date.month &&
                                calendar.get(Calendar.YEAR) == date.year
                    }
                    else -> false
                }
            }
        } ?: run {
            // 如果 selectedDate 為空，則清空 dayItems，避免不必要的計算
            Log.d("CalendarScreen", "dayItems = empt ${dayItems.size}")
            dayItems = emptyList()
        }
    }
    Log.d("CalendarScreen", "過濾後項目數量:dayItems ${dayItems.size}")
    Log.d("CalendarScreen", "過濾後項目數量:filteredItems ${filteredItems.size}")


    Column {
        // 日曆部分
        val updatedDates = uiState.dates.map { date ->
            date.copy(
                isSelected = selectedDate?.let { selected ->
                    date.dayOfMonth == selected.dayOfMonth &&
                            date.month == selected.month &&
                            date.year == selected.year
                } ?: false
            )
        }
        CalendarWidget(
            days = DateUtil.daysOfWeek,
            yearMonth = uiState.yearMonth,
            dates = updatedDates,  // 傳遞更新後的 dates 列表
            // 切換到上一個月份
            onPreviousMonthButtonClicked = { prevMonth ->
                calendarVM.toPreviousMonth(prevMonth)
            },
            // 切換到下一個月份
            onNextMonthButtonClicked = { nextMonth ->
                calendarVM.toNextMonth(nextMonth)
            },
            // 點擊日期時更新選擇的日期和顯示的筆記或揪團
            onDateClickListener = { date ->
                selectedDate = date ?: return@CalendarWidget // 確保 selectedDate 不為空
            },
        )
        // 載入提示
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            when {
                isLoading && filteredItems.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        color = FColor.Orange_1st
                    )
                }
                filteredItems.isEmpty() || dayItems.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.height(110.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier,
                            ) {
                                Text(text = "${stringResource(id = R.string.str_note_back_home_click)} ")
                                Icon(
                                    modifier = Modifier.padding(top = 1.dp),
                                    imageVector = Icons.Outlined.AddCircleOutline,
                                    contentDescription = stringResource(id = R.string.str_note_add)
                                )
                                Text(text = " ${stringResource(id = R.string.str_note_add_group)}")
                            }
                            Text(modifier = Modifier.padding(vertical = 3.dp),
                                text = "或")
                            Row(
                                modifier = Modifier,
                            ) {
                                Text(text = "${stringResource(id = R.string.str_click)} ")
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = stringResource(id = R.string.str_note_add)
                                )
                                Text(text = " ${stringResource(id = R.string.str_note_add)}")
                            }
                        }
                    }
                }
//
                // 卡片列表部分
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            count = dayItems.size,
                            key = { index ->
                                when (val item = dayItems[index]) {
                                    is Note -> "note_${item.noteId}"
                                    is Group -> "group_${item.groupId}"
                                    else -> index.toString()
                                }
                            }
                        ) { index ->
                            when (val item = dayItems[index]) {
                                is Note -> {
                                    NoteOrGroupCard(
                                        onClick = {
                                            navController.navigate("note/edit/${item.noteId}")
                                        },
                                        type = item.type,
                                        date = item.date,
                                        day = item.day,
                                        title = item.title,
                                        content = item.content,
                                        imageResId = item.imageResId,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp)
                                    )
                                }

                                is Group -> {
                                    NoteOrGroupCard(
                                        type = CardContentType.GROUP,
                                        date = item.date,         // 使用格式化後的日期
                                        day = item.day,           // 使用格式化後的星期
                                        title = item.groupName,
                                        restaurantName = item.restaurantName,
                                        restaurantAddress = item.restaurantAddress,
                                        isPublic = item.isPublic,
                                        onClick = { navController.navigate("GroupChatRoom/${item.groupId}") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp)
                                    )
                                }

                                else -> {} // 處理其他可能的情況
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalendarScreenPreview() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val calendarVM: CalendarVM = viewModel() // 取得 ViewModel
    val userVM: UserViewModel = viewModel() // 取得 ViewModel

    CalendarScreen(navController, calendarVM, userVM)

}

