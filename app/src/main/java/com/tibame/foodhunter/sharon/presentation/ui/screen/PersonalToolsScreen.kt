package com.tibame.foodhunter.sharon.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.presentation.ui.component.TabBarComponent
import com.tibame.foodhunter.sharon.presentation.viewmodel.PersonalToolsVM
import com.tibame.foodhunter.core.ui.theme.FoodHunterTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.sharon.presentation.ui.constant.TabConstants
import com.tibame.foodhunter.sharon.presentation.ui.component.topbar.CalendarTopBar
import com.tibame.foodhunter.sharon.presentation.ui.component.topbar.NoteTopBar
import com.tibame.foodhunter.sharon.presentation.viewmodel.CalendarVM
import com.tibame.foodhunter.sharon.presentation.viewmodel.NoteVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalToolsScreen(
    navController: NavHostController,
    userVM: UserViewModel,
    personalToolsVM: PersonalToolsVM
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val noteVM: NoteVM = viewModel()
    val calendarVM: CalendarVM = viewModel()

    val uiState by personalToolsVM.uiState.collectAsState()


    // 監聽 Note 搜尋事件
    LaunchedEffect(Unit) {
        personalToolsVM.noteSearchQuery.collect { query ->
            Log.d("PersonalToolsScreen", "收到 Note 搜尋事件: $query")
            noteVM.searchNotes(query)
        }
    }

    // 監聽 Calendar 搜尋事件
    LaunchedEffect(Unit) {
        personalToolsVM.calendarSearchQuery.collect { query ->
            Log.d("PersonalToolsScreen", "收到 Calendar 搜尋事件: $query")
            calendarVM.searchItems(query)
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(color = Color.White),
        topBar = {
            when (uiState.selectedTabIndex) {
                TabConstants.CALENDAR -> CalendarTopBar(
                    navController = navController,
                    scrollBehavior = scrollBehavior,
                    personalToolsVM = personalToolsVM,
                    calendarVM = calendarVM
                )

                TabConstants.NOTE -> NoteTopBar(
                    navController = navController,
                    scrollBehavior = scrollBehavior,
                    personalToolsVM = personalToolsVM
                )
            }
        },
        floatingActionButton = {
            when (uiState.selectedTabIndex) {
                in TabConstants.CALENDAR..TabConstants.NOTE ->
                    FloatingActionButton(
                        containerColor = colorResource(R.color.orange_1st),
                        contentColor = colorResource(R.color.white),
                        modifier = Modifier.padding(start = 317.dp, bottom = 76.dp),
                        onClick = { navController.navigate("note/add") },
                    ) { Icon(Icons.Default.Edit, contentDescription = "Add") }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            // 頁籤切換
            TabBarComponent(
                selectedTab = uiState.selectedTabIndex,
                onTabSelected = { personalToolsVM.updateSelectedTab(it) },
                tabList = uiState.tabList.map { stringResource(id = it) }
            )

            // 根據選中的 Tab 顯示對應的頁面
            when (uiState.selectedTabIndex) {
                TabConstants.CALENDAR -> {
                    CalendarScreen(
                        navController = navController,
                        calendarVM = calendarVM,
                        userVM = userVM
                    )
                }

                TabConstants.NOTE -> {
                    NoteScreen(
                        navController = navController,
                        noteVM = noteVM,
                        userVM = userVM,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TabMainScreenPreview() {
    // 使用 rememberNavController() 創建一個模擬的 NavController
    val mockNavController = rememberNavController()
    FoodHunterTheme {
//        PersonalToolsScreen(
//            navController = mockNavController,
//            userVM = UserViewModel()
//        )
    }
}

