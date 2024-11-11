package com.tibame.foodhunter.wei

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.ui.theme.FColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetail(
    navController: NavHostController,
    reviewVM: ReviewVM = remember { ReviewVM() }
) {
    val restaurantId = navController.previousBackStackEntry?.arguments?.getInt("restaurantId") ?: 0
    val context = LocalContext.current
    var mainSceneName by remember { mutableStateOf("評論頁面") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val destination = navController.currentBackStackEntryAsState().value?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    // 回傳CoroutineScope物件以適用於此compose環境


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
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    ReviewInfoDetail(reviewVM = reviewVM)

//                            HorizontalDivider(
//                                modifier = Modifier,
//                                thickness = 1.dp,
//                                color = FColor.Orange_1st
//                            )

                    DetailReviewZone(reviewVM = reviewVM)

                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = 2.5.dp,
                        color = FColor.Orange_1st
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewDetailScreenPreview() {
    // 提供一個模擬的 NavHostController 和 ReviewVM
    val navController = rememberNavController() // 不需要做太多複雜的導航設定
    val reviewVM = remember { ReviewVM() } // 使用模擬的 ReviewVM
    ReviewDetail(navController = navController, reviewVM = reviewVM)
}