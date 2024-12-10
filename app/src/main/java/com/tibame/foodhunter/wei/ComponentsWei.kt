package com.tibame.foodhunter.wei

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.andysearch.ImageScreen
import com.tibame.foodhunter.andysearch.SearchScreenVM
import com.tibame.foodhunter.andysearch.extractAddress
import com.tibame.foodhunter.sharon.presentation.ui.component.NiaTab
import com.tibame.foodhunter.sharon.presentation.ui.component.NiaTabRow
import com.tibame.foodhunter.core.ui.theme.FColor
import com.tibame.foodhunter.zoe.Avatar
import com.tibame.foodhunter.zoe.ImageDisplay
import com.tibame.foodhunter.zoe.ImageSource
import com.tibame.foodhunter.zoe.Post


@Preview
@Composable
fun PreviewInfoDetail() {
    //RelatedPost()

}

/**餐廳資訊*/
@Composable
fun RestaurantInfoDetail(
    navController: NavController,
    restaurantVM : SearchScreenVM
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("建立揪團", "建立筆記", "建立貼文")

    val context = LocalContext.current
    val restaurant by restaurantVM.choiceOneRest.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        restaurant?.let {
            ImageScreen(
                it.restaurant_id,
                Modifier
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .border(
                        BorderStroke(3.dp, Brush.sweepGradient(
                        listOf(
                            Color(0xFFC6826F),
                            Color(0xFFFE8160),
                            Color(0xFFFFC529),
                            Color(0xFFFFEFC3),
                            Color(0xFFFFC529),
                            Color(0xFFFE8160),
                        )
                    )
                    ), RoundedCornerShape(12))
                    .clip(RoundedCornerShape(12)),
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            //星星

            Text(
                text = restaurant?.name.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FColor.Dark_80
            )
            extractAddress(address = restaurant?.address ?: "無地址資訊", 1)?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,// 限制宽度
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )
            }
            val averageScore = if (restaurant?.total_review != 0) {
                restaurant?.total_scores?.toDouble()?.div(restaurant?.total_review!!)
            } else {
                0.0
            }
            Log.d("rating", "$restaurant, ${restaurant?.total_review}, ${restaurant?.total_review}")
            val formattedAverageScore = String.format("%.1f", averageScore)
            val text = formattedAverageScore
            Row(
                verticalAlignment = Alignment.CenterVertically // 對齊 Text 和 Icon
            ) {
                Text(
                    text = "營業中",
                    style = TextStyle(
                        color = colorResource(R.color.teal_700),
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.width(4.dp)) // 添加間距

                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )

                Spacer(modifier = Modifier.width(4.dp)) // 添加間距

                Box(
                    contentAlignment = Alignment.Center // 使兩層 Icon 居中對齊
                ) {
                    // 底層的黑色描邊 Icon（比上層的 Icon 稍大）
                    Icon(
                        painter = painterResource(R.drawable.baseline_star),
                        contentDescription = "rating",
                        modifier = Modifier.size(16.dp), // 比上層的 Icon 大一點
                        tint = Color.Black // 黑色作為描邊
                    )

                    // 上層的黃色 Icon
                    Icon(
                        painter = painterResource(R.drawable.baseline_star),
                        contentDescription = "rating",
                        modifier = Modifier.size(12.dp), // 內層 Icon 稍小
                        tint = Color.Yellow // 設置 Icon 顏色為黃色
                    )
                }
            }

        }
//        Spacer(modifier = Modifier.weight(1f))

        /**加入收藏(擱置)*/
//        Column(
//            horizontalAlignment = Alignment.End,
//            verticalArrangement = Arrangement.Top,
//            modifier = Modifier.weight(0.5f)
//        ) {
//
//            //加入收藏
//            IconButton(
//                onClick = {
//                    isBookmarked = !isBookmarked
//                    val message = if (isBookmarked) {
//                        "收藏成功"
//                    } else {
//                        "取消收藏"
//                    }
//
//                    scope.launch {
//                        Log.e("TAG", "showSnackBar")
//                        snackbarHostState.showSnackbar(
//                            message,
//                            withDismissAction = true
//                        )
//                    }
//                }) {
//                Icon(
//                    modifier = Modifier.size(30.dp),
//                    painter = painterResource(if (isBookmarked) R.drawable.bookmark_filled else R.drawable.bookmark_border),
//                    contentDescription = if (isBookmarked) "已收藏" else "未收藏",
//                )
//            }
//        }
    }

    var selectedTab by remember { mutableIntStateOf(-1) }
    val titles = listOf("營業時間", "電話", "信箱/網站")
    NiaTabRow(
        selectedTabIndex = if (selectedTab == -1) 0 else selectedTab,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        titles.forEachIndexed { index, title ->
            NiaTab(
                selected = selectedTab == index,
                onClick = { selectedTab = if (selectedTab == index) -1 else index },
                text = { Text(text = title) },
            )
        }
    }
    if (selectedTab != -1) {
        when (selectedTab) {
            0 -> {
                // 營業時間
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    restaurant?.opening_hours?.let { openingHours ->
                        openingHours.split(";").forEach { dayHours ->
                            val cleanDayHours = dayHours.trim()
                            Text(
                                text = cleanDayHours,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            1 -> {
                // 電話
                Text(
                    text = restaurant?.home_phone ?: "無電話資訊",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }

            2 -> {
                // 地址
                Text(
                    text = restaurant?.email ?: "無信箱/網站資訊",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**相關貼文*/
@Composable
fun RelatedPost(posts: List<Post>, navController: NavController) {
    Log.d("RelatedPost", posts.toString())
    val postListSize = posts.size
    val pagerState = rememberPagerState(pageCount = { postListSize })

    var loading by remember { mutableStateOf(true) }

    // 模擬加載過程，可以替換為實際的數據加載邏輯
    LaunchedEffect(posts) {
        loading = posts.isEmpty()

    }
    when {
        loading && posts.isEmpty() -> {
            // 顯示轉圈進度指示器
            Column(
                modifier = Modifier.fillMaxWidth(),
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
        }

        !loading && posts.isEmpty() -> {
            // 確定沒有貼文
            Text(
                text = "尚無貼文",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp)
            )
        }

        else -> {
            // 有貼文，顯示 HorizontalPager
            HorizontalPager(
                pageSize = PageSize.Fixed(144.dp),
                beyondViewportPageCount = 3,
                state = pagerState,
                modifier = Modifier
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color.White),
                pageSpacing = 8.dp,
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                val postId = posts[page].postId
                Log.d("postId1", postId.toString())

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .width(180.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.navigate("postDetail/${postId}") }
                ) {
                    Log.d("publisher1", "RelatedPost${posts[page].publisher}")

                    // 顯示圖片和使用者頭像
                    ImageDisplay(imageSource = ImageSource.CarouselSource(posts[page].carouselItems))
                    Avatar(
                        imageData = posts[page].publisher.avatarBitmap,
                        defaultImage = posts[page].publisher.avatarImage
                    )

                    // 帶黑色描邊的白色文字
                    Text(
                        text = posts[page].content,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(2.dp),
                        color = Color.White,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewTopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    reviewVM: ReviewVM
) {
//    BaseTopBar(
//        navController = navController,
//        scrollBehavior = scrollBehavior,
//        onSearchQueryChange = {
//            noteViewModel.selectedFilters
//        },
//        onFilter = {
//            noteViewModel.handleFilter(listOf())
//        },
//
}


