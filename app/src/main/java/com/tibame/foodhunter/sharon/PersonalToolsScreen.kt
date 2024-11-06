package com.tibame.foodhunter.sharon

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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.components.TabBarComponent
import com.tibame.foodhunter.sharon.viewmodel.PersonalToolsVM
import com.tibame.foodhunter.ui.theme.FoodHunterTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tibame.foodhunter.sharon.components.topbar.CalendarTopBar
import com.tibame.foodhunter.sharon.components.topbar.NoteTopBar
import com.tibame.foodhunter.sharon.viewmodel.CalendarVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalToolsScreen(
    navController: NavHostController,
    viewModel: PersonalToolsVM = viewModel()
) {
    // 獲取當前導航棧中的目的地，用於判斷是否顯示 TopBar 和返回按鈕
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()



    Scaffold(
        modifier = Modifier
            .fillMaxSize()  // 確保填滿
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(color = Color.White),
        topBar = {
            when (uiState.selectedTabIndex) {
                TabConstants.CALENDAR -> CalendarTopBar(
                    navController = navController,
                    scrollBehavior = scrollBehavior,
                    personalToolsVM = viewModel,
                    calendarVM = CalendarVM()
                )
                TabConstants.NOTE -> NoteTopBar(
                    navController = navController,
                    scrollBehavior = scrollBehavior,
                    personalToolsVM = viewModel
                )
            }
        },
        floatingActionButton = {
            when (uiState.selectedTabIndex) {
                in TabConstants.CALENDAR..TabConstants.NOTE  ->
                    FloatingActionButton(
                        containerColor = colorResource(R.color.orange_1st),
                        contentColor = colorResource(R.color.white) ,
                        modifier = Modifier.padding(start =317.dp, bottom = 76.dp),
                        onClick = {
                            navController.navigate("note/add")

                        },
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Add")
                    }
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
                onTabSelected = { viewModel.updateSelectedTab(it) },
                tabList = uiState.tabList.map { stringResource(id = it) }
            )

            // 根據選中的 Tab 顯示對應的頁面
            when (uiState.selectedTabIndex) {
                TabConstants.CALENDAR -> {

                    CalendarScreen(
                        navController = navController,
                        viewModel = viewModel()
                    )
                }
                TabConstants.NOTE -> {

                    NoteScreen(
                        navController = navController,
                        noteVM = viewModel()
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
        // 調用你要預覽的 UI 函數
        PersonalToolsScreen(navController = mockNavController)
    }
}

