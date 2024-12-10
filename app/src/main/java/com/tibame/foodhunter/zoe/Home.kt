package com.tibame.foodhunter.zoe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.sharon.presentation.ui.component.NiaTab
import com.tibame.foodhunter.sharon.presentation.ui.component.NiaTabRow

@Composable
fun Home(
    navController: NavHostController,
    initTab: Int,
    memberId: Int,
    userVM: UserViewModel,  // 確保從外部接收 userVM
    postViewModel: PostViewModel = viewModel()
) {


    var selectedTab by remember { mutableIntStateOf(initTab) }


    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),

        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        PostTabBarComponent(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // 根據選中的 Tab 顯示對應的頁面
        when (selectedTab) {
            0 -> RecommendedPosts(  navController = navController,
                postViewModel = postViewModel,
                userVM = userVM)
            1 -> SearchPost(navController)


        }


    }
}

@Composable
fun PostTabBarComponent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,

    ) {
    val tabList = listOf(
        stringResource(id = com.tibame.foodhunter.R.string.str_Recommended_posts),
        stringResource(id = com.tibame.foodhunter.R.string.str_searchpost)

    )

    NiaTabRow(selectedTabIndex = selectedTab) {
        tabList.forEachIndexed { index, title ->
            NiaTab(
                text = { Text(text = title) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}




//
//
//
//
//@Preview(showBackground = true)
//@Composable
//fun HomePreview() {
//    val mockNavController = rememberNavController()
//    FoodHunterTheme  {
//       Home(navController = mockNavController, 0)
//    }
//
//
//
//}
