package com.tibame.foodhunter.zoe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.ui.theme.FColor
import kotlinx.coroutines.launch

@Composable
fun RecommendedPosts(
    navController: NavHostController? = null,
    postViewModel: PostViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
) {
    val filteredPosts by postViewModel.getFilteredPosts().collectAsState()
    val selectedFilters by postViewModel.selectedFilters.collectAsState()
    // 取得當前用戶ID
    val currentUserId = 7

    // 新增loading state
    var isLoading by remember { mutableStateOf(true) }

    // 使用LaunchedEffect來模擬資料載入
    LaunchedEffect(filteredPosts) {
        isLoading = filteredPosts.isEmpty()
    }

    Column {
        FilterChips(
            filters = listOf("早午餐", "午餐", "晚餐", "下午茶", "宵夜"),
            selectedFilters = selectedFilters,
            onFilterChange = { updatedFilters ->
                postViewModel.updateFilters(updatedFilters)
            }
        )

        // 根據loading狀態顯示不同內容
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = FColor.Orange_1st
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "載入中...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FColor.Orange_1st
                    )
                }
            } else {
                PostList(
                    posts = filteredPosts,
                    viewModel = postViewModel,
                    currentUserId = currentUserId ?: -1,
                    onUserClick = { publisherId ->
                        navController?.navigate("person_homepage/$publisherId") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun PostList(
    posts: List<Post>,
    viewModel: PostViewModel,
    currentUserId: Int,
    onUserClick: (Int) -> Unit // 新增參數用於處理用戶點擊
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(posts) { post ->
            PostItem(
                post = post,
                viewModel = viewModel,
                currentUserId = currentUserId,
                onUserClick = onUserClick // 傳遞點擊事件處理函數
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    post: Post,
    viewModel: PostViewModel,  // 添加 ViewModel
    currentUserId: Int,        // 添加當前用戶 ID
    onUserClick: (Int) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            PostHeader(
                post = post,
                onUserClick = onUserClick // 傳遞點擊事件
            )

            Spacer(modifier = Modifier.height(8.dp))

            ImageDisplay(imageSource = ImageSource.CarouselSource(post.carouselItems))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FavoriteIcon()

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.chat_bubble_outline_24),
                                contentDescription = "Chat Bubble",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // 顯示留言數量
                        Text(
                            text = "${post.comments.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

//                Icon(
//                    painter = painterResource(id = R.drawable.baseline_bookmark_border_24),
//                    contentDescription = "Bookmark",
//                    modifier = Modifier.size(24.dp)
//                )
            }

            Text(
                text = post.content,
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            MessageSheet(
                post = post,
                viewModel = viewModel,
                currentUserId = currentUserId,
                onConfirm = {
                    scope.launch {
                        sheetState.hide()
                        showBottomSheet = false
                    }
                }
            )
        }
    }
}
@Composable
private fun PostHeader(
    post: Post,
    onUserClick: (Int) -> Unit // 新增點擊事件回調
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            imageData = post.publisher.avatarBitmap,
            defaultImage = post.publisher.avatarImage,
            onClick = { onUserClick(post.publisher.id) },
            contentDescription = "Avatar for ${post.publisher.name}"
        )

        Column(
            modifier = Modifier
                .clickable { onUserClick(post.publisher.id) }
        ) {
            Text(
                text = post.publisher.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = post.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
