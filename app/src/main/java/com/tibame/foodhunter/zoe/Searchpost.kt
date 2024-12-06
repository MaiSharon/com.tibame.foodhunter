package com.tibame.foodhunter.zoe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.ui.theme.FColor
import com.tibame.foodhunter.ui.theme.FoodHunterTheme

@Composable
fun SearchPost(
    navController: NavHostController,
    postViewModel: PostViewModel = viewModel()
) {
    val selectedFilters by postViewModel.selectedFilters.collectAsState()
    val filteredPosts by postViewModel.getFilteredPosts().collectAsState()
    val searchQuery by postViewModel.searchQuery.collectAsState() // Use ViewModel's searchQuery

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        com.tibame.foodhunter.sharon.components.SearchBar(
            query = searchQuery,
            onQueryChange = { postViewModel.updateSearchQuery(it) }, // Update through ViewModel
            placeholder = {
                Text(
                    "搜尋",
                    color = FColor.Gary,
                    fontSize = 16.sp
                )
            },
        )

        FilterChips(
            filters = listOf( "早午餐", "健康餐", "韓式", "日式", "義式", "美式", "中式",  "下午茶", "甜點", "素食"),
            selectedFilters = selectedFilters,
            onFilterChange = { updatedFilters ->
                postViewModel.updateFilters(updatedFilters)
            }
        )

        ImageList(
            posts = filteredPosts,
            onPostClick = { postId ->
                postViewModel.setPostId(postId)
                navController.navigate("postDetail/$postId")
            }
        )
    }
}

@Composable
fun FilterChips(
    filters: List<String>,                 // 可用的標籤列表
    selectedFilters: List<String>,         // 當前選中的標籤
    onFilterChange: (List<String>) -> Unit // 選中狀態變更時的回調
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilters.contains(filter)

            // 使用 FilterChip 或 Chip 來表示篩選標籤
            FilterChip(
                selected = isSelected,
                onClick = {
                    val updatedFilters = if (isSelected) {
                        selectedFilters - filter
                    } else {
                        selectedFilters + filter
                    }
                    onFilterChange(updatedFilters)
                },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    selectedContainerColor = colorResource(R.color.orange_3rd) // 設定選中顏色
                ),
                border = if (isSelected) {
                    BorderStroke(2.dp, colorResource(R.color.orange_5th)) // 設定選中時的邊界顏色
                } else {
                    BorderStroke(2.dp, color= Color.LightGray)
                }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SerchpostPreview() {
    FoodHunterTheme  {
        SearchPost(rememberNavController())
    }
}
