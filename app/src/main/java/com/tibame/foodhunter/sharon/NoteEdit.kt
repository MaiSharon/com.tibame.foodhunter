package com.tibame.foodhunter.sharon

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.andysearch.SearchScreenVM
import com.tibame.foodhunter.andysearch.ShowGoogleMap
import com.tibame.foodhunter.andysearch.ShowRestaurantLists
import com.tibame.foodhunter.sharon.components.SearchBar
import com.tibame.foodhunter.sharon.components.topbar.NoteEditTopBar
import com.tibame.foodhunter.sharon.viewmodel.NoteEditEvent
import com.tibame.foodhunter.sharon.viewmodel.NoteEditVM
import com.tibame.foodhunter.ui.theme.FColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddNotePreview() {
    val mockNavController = rememberNavController()
}

/**
 * 定義筆記編輯頁面的導航類型
 * sealed class 確保所有可能的導航類型都被明確定義，不會有其他意外的類型
 */
sealed class NoteEditNavigation {
    /**
     * 新增筆記的導航類型
     * data object 用於不需要攜帶額外數據的類型，相當於一個單例對象
     */
    data object Add : NoteEditNavigation()

    /**
     * 編輯筆記的導航類型
     * data class 用於需要攜帶數據(noteId)的類型
     * @param noteId 要編輯的筆記ID
     */
    data class Edit(val noteId: Int) : NoteEditNavigation()
}

/**
 * 筆記編輯頁面的路由組件
 * 負責根據不同的導航類型顯示相應的UI
 *
 * @param navController 導航控制器，用於處理頁面導航
 * @param navigation 導航類型，決定是新增還是編輯模式
 * @param noteId 筆記ID，用於編輯模式
 * @param noteEditVM 筆記編輯的ViewModel，使用 noteEditVM() 委託創建
 */
@Composable
fun NoteEditRoute(
    navController: NavHostController,
    navigation: NoteEditNavigation,
    noteEditVM: NoteEditVM = viewModel(),
    userVM: UserViewModel,
    noteId: Int? = null
) {
    val memberId by userVM.memberId.collectAsStateWithLifecycle()

    // 設置會員ID
    LaunchedEffect(memberId) {
        noteEditVM.setMemberId(memberId)
    }

    // 處理不同的導航類型
    when (navigation) {
        // 新增模式，顯示空白頁面
        is NoteEditNavigation.Add -> {
            NoteEdit(
                navController = navController,
                noteEditVM = noteEditVM,
                isNewNote = true  // 標記為新增模式
            )
        }
        // 編輯模式，載入資料
        is NoteEditNavigation.Edit -> {
            // 收集現有筆記數據
            val uiState by noteEditVM.uiState.collectAsState()

            LaunchedEffect(navigation.noteId) {
                Log.d("NoteEditRoute", "開始載入筆記 ID: ${navigation.noteId}")
                noteEditVM.loadNote(navigation.noteId)
            }

            if (uiState.isLoading) {
                // 顯示載入指示器
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    color = FColor.Orange_1st
                )
            } else {
                // 載入完成，顯示編輯頁面
                NoteEdit(
                    navController = navController,
                    noteEditVM = noteEditVM,
                    isNewNote = false,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEdit(
    navController: NavHostController = rememberNavController(),
    noteEditVM: NoteEditVM,
    isNewNote: Boolean,
) {
    val uiState by noteEditVM.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    // 定義樣式
    val editNoteBasicTextStyle = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight(500),
        color = Color(0xFF9A9FAE),
    )

    val editNoteTitleTextStyle = editNoteBasicTextStyle.copy(
        fontSize = 30.sp,
        fontWeight = FontWeight(500),
    )

    val editNoteBodyTextStyle = editNoteBasicTextStyle.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight(400)
    )

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Log.d("NoteEdit", "當前UI狀態: $uiState")  // 加入日誌

    // 2. 檢查是否有內容要顯示
    if (!isNewNote && uiState.title.isEmpty()) {
        Log.d("NoteEdit", "等待內容載入...")
        // 可以顯示載入指示器

    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).background(Color.White),
        topBar = {
            NoteEditTopBar(
                navController = navController,
                scrollBehavior = scrollBehavior,
                noteEditVM = noteEditVM
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 34.dp,
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 34.dp,
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        )
        {
            Box(  // 輸入標題
                modifier = Modifier
                    .height(44.dp)
                    .padding(bottom = 2.dp),
                contentAlignment = Alignment.TopStart
            ) {
                BasicTextField(
                    value = uiState.title,
                    onValueChange = { title ->
                        if (title.length <= 10) {
                            noteEditVM.onEvent(NoteEditEvent.UpdateTitle(title))
                        }
                    },
                    textStyle = editNoteTitleTextStyle,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // 收起鍵盤
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                )
                if (uiState.title.isEmpty()) {
                    Text(
                        text = "請輸入標題",
                        style = editNoteTitleTextStyle,
                    )
                }
            }
            // 顯示餐廳、顯示日期
            Row(
                modifier = Modifier.height(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            ) {
                VerticalLine()
//                // 只在有選中餐廳時顯示 DisplayRestaurantChip
//                if (uiState.restaurantId?.isNotEmpty() == true) {
//                    DisplayRestaurantChip(
//                        label = uiState.restaurantName ?: "",
//                        onClear = {
//                            noteEditVM.onEvent(NoteEditEvent.UpdateRestaurant(null))
//                        }
//                    )
//                    VerticalLine()
//                }
//
//                // 只在沒有選中餐廳時顯示 SelectRestaurantChip
//                if (uiState.restaurantName == null) {
//                    SelectRestaurantChip(
//                        onClick = { isBottomSheetVisible = true },
//                        selectedRestaurant = true  // 因為只在需要選擇時顯示，所以直接設為 true
//                    )
//                    VerticalLine()
//                }

                // 日期顯示
                DisplayDateChip(
                    initialDate = uiState.selectedDate,
                    onDateSelected = { selectedDate ->
                        noteEditVM.onEvent(NoteEditEvent.UpdateDate(selectedDate))
                    }
                )
            }
            Box(  // 輸入內文
                modifier = Modifier
                    .heightIn(min = 24.dp)
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                // 失去焦點時才隱藏鍵盤
                                keyboardController?.hide()
                            }
                        },
                    value = uiState.content,
                    onValueChange = { content ->
                        if (content.length <= 500) {
                            noteEditVM.onEvent(NoteEditEvent.UpdateContent(content))
                        }
                    },
                    textStyle = editNoteBodyTextStyle,
                )
                if (uiState.content.isEmpty()) {
                    Text(
                        text = "請輸入內文",
                        style = editNoteBodyTextStyle,
                    )
                }
            }

            // 選擇餐廳的 BottomSheet
            if (isBottomSheetVisible) {
                SelectRestaurantBottomSheet(
                    onRestaurantPicked = { restaurant ->
                        noteEditVM.onEvent(NoteEditEvent.UpdateRestaurant(restaurant))
                        isBottomSheetVisible = false // 當選到餐廳就關閉 BottomSheet
                    },
                    onClose = { isBottomSheetVisible = false } // 統一關閉 BottomSheet
                )
            }

        }
    }
}

@Composable
fun VerticalLine() {
    Box(
        modifier = Modifier
            .width(8.dp)
            .height(20.dp)
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(Color.Gray)
                .align(Alignment.Center)
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayDateChip(
    initialDate: Date = Date(),
    onDateSelected: (Date) -> Unit = {}
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var currentDate by remember { mutableStateOf(initialDate) }


    // 日期格式化
    val displayFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    // 設置 DatePicker 的初始狀態
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.time,  // 使用 time 符合毫秒
        initialDisplayMode = DisplayMode.Picker
    )

    FilterChip(
        modifier = Modifier.height(32.dp),
        onClick = { showDatePicker = true },
        label = {
            Text(text = displayFormatter.format(currentDate))
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FColor.Orange_5th,
            selectedLabelColor = FColor.Dark_80,
            containerColor = Color.White
        ),
        selected = true,
    )

    if (showDatePicker) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = FColor.Orange_6th
            ),
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    // 點擊確定按鈕時呼叫onConfirm(Long?)並將選取日期傳入以回饋給原畫面
                    onClick = {
                        datePickerState.selectedDateMillis?.let { milliseconds ->
                            val newDate = Date(milliseconds)  // 轉換成 Date
                            currentDate = newDate
                            onDateSelected(newDate)
                        }
                        showDatePicker = false                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FColor.Orange_1st
                    )
                ) {
                    Text("確認")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FColor.Orange_5th
                    )
                ) {
                    Text("取消", color = Color.Black)
                }
            }

        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = FColor.Orange_6th,
                    todayContentColor = FColor.Dark_80,
                    selectedDayContainerColor = FColor.Orange_1st,
                    disabledSelectedDayContainerColor = FColor.Orange_1st,
                    todayDateBorderColor = FColor.Orange_1st
                )

            )
        }
    }
}
@Composable
fun SelectRestaurantChip(
    onClick: () -> Unit,
    selectedRestaurant: Boolean = false,
) {
    FilterChip(
        // 選擇餐廳時，點擊 > bottom sheet > bottom sheet選擇完畢 > Show標籤函數
        onClick = onClick,
        // 有選擇到餐廳，為選取狀態
        selected = selectedRestaurant,
        label = {
            if (selectedRestaurant) {
                Text("選擇餐廳")
            } else {
                Text("  ")

            }
        },
    )
}


@Composable
fun DisplayRestaurantChip(
    label: String,
    onClear: () -> Unit,
) {
    // 只有當 label 非空時才渲染 FilterChip
    if (label.isNotEmpty()) {
        FilterChip(
            onClick = {},
            selected = true,
            label = {
                // TODO: 點擊後跳轉到餐廳資訊頁面
                Text(label)
            },
            enabled = true,
            trailingIcon = {
                IconButton(
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                    onClick = onClear
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRestaurantBottomSheet(
    onRestaurantPicked: (String) -> Unit,
    onClose: () -> Unit, // 關閉 BottomSheet 的回調
) {
    // 開滿頁面或開一半
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    // 預覽顯示用，但尚未測試成功
    val standardSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )
    val coroutineScope = rememberCoroutineScope()


    // chip 點選後 出現Bottom sheet
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = modalSheetState,
        onDismissRequest = {
            coroutineScope.launch { modalSheetState.hide() }
            onClose() // 關閉 BottomSheet，在外部控制
        },
        scrimColor = Color.Black.copy(alpha = 0.5f), // 半透明灰色背景
        properties = ModalBottomSheetProperties(
            isFocusable = true,  // 允許接收焦點，例如接收TextFiled輸入事件
            shouldDismissOnBackPress = true,
            securePolicy = SecureFlagPolicy.SecureOff, // 關閉，防止應用窗口的內容被截圖或錄屏
        ),
        content = {
            BottomSheetContent(
                onRestaurantPicked = { restaurant ->
                    onRestaurantPicked(restaurant) // 傳回選定餐廳
                    coroutineScope.launch { modalSheetState.hide() }
                    onClose() // 關閉 BottomSheet，在外部控制
                },
                onClose = {
                    coroutineScope.launch { modalSheetState.hide() }
                    onClose() // 關閉 BottomSheet，在外部控制
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    onClose: () -> Unit, // 設定 Bottom Sheet 關閉的回調參數
    onRestaurantPicked: (String) -> Unit, // 餐廳資訊回調

) {

    val testVM: SearchScreenVM = viewModel()
    val test_restaurant by testVM.preRestaurantList.collectAsState()
    Log.d("test_restaurant", "${test_restaurant}")
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    // 頂部工具欄
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(Color.Blue),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "選擇餐廳",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.alpha(0f) // 設置透明度
            ) {
                Icon(Icons.Filled.Check, contentDescription = "")
            }
        },
        actions = {
            IconButton(onClick = { onClose() }) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }
        }
    )

    // 水平分隔線
    HorizontalDivider(
        color = Color.Gray,
        thickness = 1.dp
    )
    // 餐廳選擇按鈕
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color.Blue)
    ) {
        // 餐廳選擇按鈕，選擇餐廳後執行回調並關閉 BottomSheet
        Button(onClick = {
            onRestaurantPicked("肯德基") // 選擇餐廳
            onClose() // 關閉 BottomSheet
        }) {
            Text("我是肯德基")
        }

        // TODO(接入餐廳)
//        RestaurantSelectionScreen { selectedRestaurant ->
//            onRestaurantPicked(selectedRestaurant)
//            onClose()
//        }

    }

    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    SearchBar(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        placeholder = {
            Text(
                "搜尋",
                color = FColor.Gary,
                fontSize = 16.sp
            )
        },
        onSearch = {
            scope.launch { testVM.updateSearchRest(searchQuery) }
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ShowGoogleMap(
            modifier = Modifier
                .weight(0.7f)
                .padding(16.dp),
            restaurants = test_restaurant,
            restaurantVM = testVM,
            onLocationUpdate = { location -> currentLocation = location }
        )
        ShowRestaurantLists(
            restaurants = test_restaurant,
            state = false,
            currentLocation = currentLocation,
            searchTextVM = testVM,
            cardClick = { choiceRest ->
                onRestaurantPicked(choiceRest?.name.toString())
                onClose()
            }
        )
    }
}