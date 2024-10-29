package com.tibame.foodhunter.zoe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.CalendarScreen
import com.tibame.foodhunter.sharon.FavoriteScreen
import com.tibame.foodhunter.sharon.NiaTab
import com.tibame.foodhunter.sharon.NiaTabRow
import com.tibame.foodhunter.sharon.NoteScreen
import com.tibame.foodhunter.sharon.TabBarComponent
import com.tibame.foodhunter.ui.theme.FoodHunterTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home( navController: NavHostController,
          initTab: Int) {
    val samplePosts: List<Post> = getSamplePosts()
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    val context = LocalContext.current
    val destination = navController.currentBackStackEntryAsState().value?.destination
    // 當前選到的Tab
    var selectedTab by remember { mutableIntStateOf(initTab) }

    // 根據選擇的篩選標籤過濾貼文
    val filteredPosts = if (selectedFilters.isEmpty()) {
        samplePosts
    } else {
        samplePosts.filter { post -> selectedFilters.contains(post.postTag) }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        TabBarComponent(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // 根據選中的 Tab 顯示對應的頁面
        when (selectedTab) {
            0 -> CalendarScreen(navController) {}
            1 -> SearchPost(navController)
            2 -> FavoriteScreen(navController)
        }

        // 使用 FilterChips 函數
        FilterChips(
            filters = listOf("早午餐", "午餐", "晚餐"),
            selectedFilters = selectedFilters,
            onFilterChange = { updatedFilters -> selectedFilters = updatedFilters }
        )

        // 顯示過濾後的貼文列表
        PostList(posts = filteredPosts)

    }
}


@Composable
fun TabBarComponent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabList = listOf(
        stringResource(id = R.string.str_calendar),
        stringResource(id = R.string.str_note),
        stringResource(id = R.string.str_favorite)
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

// 示例數據
fun getSamplePosts(): List<Post> {
    // 示例 Publisher 資料
    val alice = Publisher(
        id = "1",
        name = "Alice",
        avatarImage = R.drawable.user1,
        joinDate = "2023-01-01",
        followers = emptyList(),
        following = emptyList()
    )

    val bob = Publisher(
        id = "2",
        name = "Bob",
        avatarImage = R.drawable.user2,
        joinDate = "2022-12-15",
        followers = emptyList(),
        following = emptyList()
    )

    val cathy = Publisher(
        id = "3",
        name = "Cathy",
        avatarImage = R.drawable.user3,
        joinDate = "2021-11-20",
        followers = emptyList(),
        following = emptyList()
    )

    // 示例 Post 資料
    return listOf(
        Post(
            id = "p1",
            publisher = alice,
            content = "今天吃了美味的早餐！",
            location = "Taipei",
            timestamp = "2 小時前",
            postTag = "早午餐",
            carouselItems = listOf(
                CarouselItem(0, R.drawable.breakfast_image_1, "Breakfast image 1"),
                CarouselItem(1, R.drawable.breakfast_image_2, "Breakfast image 2"),
                CarouselItem(2, R.drawable.breakfast_image_3, "Breakfast image 3")
            )
        ),
        Post(
            id = "p2",
            publisher = bob,
            content = "午餐是超棒的壽司！",
            location = "Kaohsiung",
            timestamp = "5 小時前",
            postTag = "午餐",
            carouselItems = listOf(
                CarouselItem(0, R.drawable.sushi_image_1, "Sushi image 1"),
                CarouselItem(1, R.drawable.sushi_image_2, "Sushi image 2")
            )
        ),
        Post(
            id = "p3",
            publisher = cathy,
            content = "晚餐牛排超好吃！",
            location = "Taichung",
            timestamp = "1 天前",
            postTag = "晚餐",
            carouselItems = listOf(
                CarouselItem(0, R.drawable.steak_image, "Steak image 1")
            )
        )
    )
}


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    FoodHunterTheme  {
       Home(rememberNavController())
    }
}
