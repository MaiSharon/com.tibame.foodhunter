package com.tibame.foodhunter.sharon

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.data.Book
import com.tibame.foodhunter.sharon.util.CalendarUiState
import com.tibame.foodhunter.sharon.util.DateUtil
import com.tibame.foodhunter.sharon.util.getDisplayName
import com.tibame.foodhunter.sharon.viewmodel.BookViewModel
import com.tibame.foodhunter.sharon.viewmodel.CalendarVM


/**
 * Created by meyta.taliti on 20/05/23.
 */

@Preview(showSystemUi = true)
@Composable
fun CalendarAppPreview() {
    CalendarApp()
}

@Composable
fun BookListComposable(
    books: List<Book>, // 傳入書籍列表
    modifier: Modifier = Modifier, // 用來自訂佈局
    onItemClick: (Book) -> Unit, // 點擊書籍項目時執行的動作
    onEditClick: (Book) -> Unit, // 編輯書籍
    onDeleteClick: (Book) -> Unit, // 刪除書籍
    showMore: Boolean, // 是否顯示編輯和刪除按鈕
) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(books) { book ->
            ListItem(
                // 讓項目可被點擊，並設定點擊時欲執行內容
                modifier = Modifier.clickable { onItemClick(book) },
                headlineContent = { Text(book.name) },
                supportingContent = { Text(book.price.toString()) },
                leadingContent = {
                    Image(
                        painter = painterResource(id = book.image),
                        contentDescription = "book"
                    )
                },
                trailingContent = {
                    if (showMore) {
                        Row {
                            // 編輯按鈕
                            IconButton(onClick = { onEditClick(book) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "edit")
                            }
                            // 刪除按鈕
                            IconButton(onClick = { onDeleteClick(book) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "delete")
                            }
                        }
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarApp(
    viewModel: CalendarVM = viewModel(),
    bookViewModel: BookViewModel = viewModel(), // 書籍 ViewModel

) {
    val uiState by viewModel.uiState.collectAsState()
    val books by bookViewModel.bookState.collectAsState() // 取得書籍狀態


    // 初始化當天的選中日期
    var selectedDate by remember {
        mutableStateOf<CalendarUiState.Date?>(null)  // 初始化為 null
    }

    // 在 LaunchedEffect 中設置初始日期
    LaunchedEffect(Unit) {
        if (selectedDate == null) {  // 只在 selectedDate 為 null 時設置
            val today = LocalDate.now()
            selectedDate = uiState.dates.find { date ->
                date.dayOfMonth == today.dayOfMonth.toString() &&
                        date.month == today.monthValue &&
                        date.year == today.year
            }
        }
    }

//    var selectedBooks by remember {
//        mutableStateOf(emptyList<Book>())
//    }
//
//    // 當 selectedDate 改變時更新 selectedBooks
//    LaunchedEffect(selectedDate, books) {
//        selectedBooks = books.filter { book ->
//            selectedDate?.let { date ->
//                book.date.dayOfMonth.toString() == date.dayOfMonth &&
//                        book.date.monthValue == date.month &&
//                        book.date.year == date.year
//            } ?: false
//        }
//    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding() - 12.dp
            )
        ) {
            // 更新日期列表，使得用戶選中的日期顯示選中狀態
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
                    viewModel.toPreviousMonth(prevMonth)
                },

                // 切換到下一個月份
                onNextMonthButtonClicked = { nextMonth ->
                    viewModel.toNextMonth(nextMonth)
                },

                // 點擊日期時更新選擇的日期和顯示的書籍
                onDateClickListener = { date ->
                    selectedDate = date
                },

            )
        }
    }
}

@Composable
fun CalendarWidget(
    days: Array<String>,
    yearMonth: YearMonth,
    dates: List<CalendarUiState.Date>,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
    onDateClickListener: (CalendarUiState.Date) -> Unit,


) {

    Column(
        modifier = Modifier
//            .fillMaxSize()
//            .wrapContentHeight(Alignment.CenterVertically)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)

    ) {
        Header(
            yearMonth = yearMonth,
            onPreviousMonthButtonClicked = onPreviousMonthButtonClicked,
            onNextMonthButtonClicked = onNextMonthButtonClicked
        )

        Row {
            repeat(days.size) {
                val item = days[it]
                DayItem(item, modifier = Modifier.weight(1f))
            }
        }

        Content(
            dates = dates,
            onDateClickListener = onDateClickListener
        )
    }
}

@Composable
fun Header(
    yearMonth: YearMonth,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),

            onClick = {
            onPreviousMonthButtonClicked.invoke(yearMonth.minusMonths(1))
        }) {
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),

                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.str_back)

            )
        }
        Text(
            text = yearMonth.getDisplayName(),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        IconButton(
            onClick = {
                onNextMonthButtonClicked.invoke(yearMonth.plusMonths(1))
            }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.next)
            )
        }
    }
}

@Composable
fun DayItem(day: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = day,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium, // 可選，設置字體粗細
                ),
            color = colorResource(R.color.dark),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp)
        )
    }
}

// 更新 Content 組件
@Composable
fun Content(
    dates: List<CalendarUiState.Date>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
    Column {
        var index = 0
        repeat(6) {
            if (index >= dates.size) return@repeat
            Row {
                repeat(7) {
                    val item = if (index < dates.size) dates[index] else CalendarUiState.Date.Empty
                    ContentItem(
                        date = item,
                        onClickListener = onDateClickListener,
                        modifier = Modifier.weight(1f)
                    )
                    index++
                }
            }
        }
    }
}

@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 判斷是否為今天
    val today = LocalDate.now()
    val isToday = date.dayOfMonth == today.dayOfMonth.toString() &&
            date.month == today.monthValue &&
            date.year == today.year

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClickListener(date) }
                .background(
                    color = when {
                        date.isSelected -> colorResource(id = R.color.gary_20)
                        isToday -> colorResource(id = R.color.gary_20)
                        else -> Color.Transparent
                    },
                    shape = CircleShape
                )
                .then(
                    when {
                        (date.isSelected && isToday) || (isToday)-> {
                        Modifier.border(
                            width = 2.dp,
                            color = colorResource(id = R.color.dark_20),
                            shape = CircleShape
                            )
                        }

                        else -> Modifier
                    }

                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 日期文字
            Text(
                text = date.dayOfMonth,
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.dark_80),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            // 如果有書籍，顯示指示點
            if (date.hasBook) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = if (date.isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = if (date.isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                }
            }

        }
    }
}


