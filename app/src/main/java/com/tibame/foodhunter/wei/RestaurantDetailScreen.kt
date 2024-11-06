@file:OptIn(ExperimentalMaterial3Api::class)

package com.tibame.foodhunter.wei

import com.tibame.foodhunter.R


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text

import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.tibame.foodhunter.andysearch.SearchScreenVM
import com.tibame.foodhunter.ui.theme.FColor
import com.tibame.foodhunter.ui.theme.FoodHunterTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetail(
    navController: NavHostController,
    restaurantVM: SearchScreenVM
) {
    val restNavController = rememberNavController()
    val context = LocalContext.current
    var mainSceneName by remember { mutableStateOf(context.getString(R.string.restaurantDetail)) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    // 回傳CoroutineScope物件以適用於此compose環境
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .weight(1f),
            topBar = {},
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                NavHost(
                    navController = restNavController,
                    startDestination = mainSceneName,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(route = mainSceneName) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            Spacer(modifier = Modifier)

                            RestaurantInfoDetail(snackbarHostState = snackbarHostState)

                            HorizontalDivider(
                                modifier = Modifier,
                                thickness = 1.5.dp,
                                color = FColor.Orange_1st
                            )
                            Spacer(modifier = Modifier.size(20.dp))

                            //社群預覽

//                            Text(
//                                text = "社群預覽  待修",
//                                fontSize = 18.sp
//                            )
//                            RelatedPosts("")


                            HorizontalDivider(
                                modifier = Modifier,
                                thickness = 1.5.dp,
                                color = FColor.Orange_1st
                            )
                            Spacer(modifier = Modifier.size(10.dp))

                            //評論顯示區
                            Text(
                                text = "評論(%評論數)",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            ReviewZone()
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RestaurantDetailPreview() {
    // 提供一個模擬的 NavHostController 和 SearchScreenVM
    val navController = rememberNavController()
    val restaurantVM = SearchScreenVM() // 根據需要替換成模擬或預設的 ViewModel

    RestaurantDetail(navController = navController, restaurantVM = restaurantVM)
}
