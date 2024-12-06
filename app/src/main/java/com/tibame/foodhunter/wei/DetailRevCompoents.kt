package com.tibame.foodhunter.wei

import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.BuildConfig
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.components.SearchBar
import com.tibame.foodhunter.ui.theme.FColor


/**詳細評論內容*/
@Composable
fun ReviewInfoDetail(
    reviewVM: ReviewVM,
) {
    val searchKeyword by reviewVM.searchKeyWord.collectAsState()
    var isActive by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.height(60.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {

            Row(modifier = Modifier)
            {
                SearchBar(
                    query = searchKeyword,
                    onQueryChange = {
                        reviewVM.updateSearchKeyword(it)
                    },
                    onSearch = { isActive = false },

                    //清除按鈕
//                trailingIcon = {
//                    if (searchKeyword.isNotEmpty()) {
//                        IconButton(onClick = { reviewVM.updateSearchKeyword("")}) {
//                            Icon(
//                                imageVector = Icons.Default.Clear,
//                                contentDescription = "清除搜尋"
//                            )
//                        }
//                    }
//                }
                )
            }
        }
    }
}


/**詳細評論顯示區*/
@Composable
fun DetailReviewZone(
    navController: NavHostController?,
    viewModel: ReviewVM,
    restaurantId: Int,
    reviewId: Int? = null
) {
    // 當 reviewId 不為 null 時才載入評論
    LaunchedEffect(reviewId) {
        reviewId?.let { id ->
            viewModel.loadReviewById(id)
        }
    }
    // 收集評論狀態
    val currentReview by viewModel.currentReview.collectAsState()
    val options = listOf("最新發布", "最多好評", "最高評分")
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier,
            thickness = 2.dp,
            color = Color(0xFFFE724C)
        )
        Spacer(modifier = Modifier.size(8.dp))


        // DropDownMenu開關按鈕
        Box(
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = painterResource(id = R.drawable.filter_list),
                    contentDescription = "評論篩選器",
                    modifier = Modifier.size(25.dp)
                )
            }

            // DropDownMenu 顯示選項
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            // 根據選擇更新排序方式
                            when (option) {
                                "最新發布" -> viewModel.updateSortOrder(SortOrder.NEWEST)
                                "最多好評" -> viewModel.updateSortOrder(SortOrder.MOST_LIKED)
                                "最高評分" -> viewModel.updateSortOrder(SortOrder.HIGHEST_RATING)
                            }
                            expanded = false // 選擇後關閉下拉選單
                        }
                    )
                }
            }
        }

        currentReview?.let { review ->
            DetailReviewItem(review = review)
        } ?: run {
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())    // 添加垂直捲動功能
                .padding(horizontal = 10.dp)
        ) {
            if (BuildConfig.DEBUG && navController == null) {
                // 在Preview模式下使用假資料
                DummyReviewList(dummyReviewList)
            } else {
                // 在實際運行時使用真實資料
                DetailReviewList(restaurantId = restaurantId, viewModel = viewModel)
            }
        }
    }
}

/** 真正的評論 */
@Composable
fun DetailReviewList(restaurantId: Int, viewModel: ReviewVM) {
    // 當頁面載入時，載入該餐廳的評論
    LaunchedEffect(restaurantId) {
        Log.d("ReviewList", "Loading reviews for restaurantId: $restaurantId")
        viewModel.loadReviews(restaurantId)
    }

    val reviews by viewModel.reviewState.collectAsState()  // 觀察評論列表的資料變動
    Log.d("ReviewList", "Loaded reviews: ${reviews.size}")

    val reviewsWithReplies = reviews.map { review ->
        review.copy(replies = generateDummyReplies())
    }

    Column {
        reviewsWithReplies.forEach { review -> // 使用包含回覆的評論列表
            DetailReviewItem(review)
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
fun DetailReviewItem(review: Reviews) {
    var isLiked by remember { mutableStateOf(review?.isLiked ?: false) }
    var isDisliked by remember { mutableStateOf(review?.isDisliked ?: false) }
    var thumbsUpCount by remember { mutableStateOf(review?.thumbsup ?: 0) }
    var thumbsDownCount by remember { mutableStateOf(review?.thumbsdown ?: 0) }
    var isExpanded by remember { mutableStateOf(false) }  // 控制內容展開/摺疊
    var showReplies by remember { mutableStateOf(false) }
    if (review == null) {
        Text(
            text = "目前尚無評論",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        return
    }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(FColor.Orange_5th),


    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(1f)
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.size(8.dp))

            // 顯示評論者名稱
            Text(
                text = review.reviewer.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FColor.Dark_80
            )

            Spacer(modifier = Modifier.size(5.dp))

            // 顯示評分
            RatingBar(
                rating = review.rating,
                onRatingChanged = { }  //顯示用，不需改變
            )

            Column {
                if (isExpanded) {
                    // 顯示評論內容
                    Text(
                        text = review.content,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                } else {
                    // 最多顯示3行
                    Text(
                        text = review.content,
                        fontSize = 16.sp,
                        color = Color.Black,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
                // 如果內容超過3行，顯示展開/收起按鈕
                if (review.content.lines().size > 3) {
                    Text(
                        text = if (isExpanded) "收起" else "...更多",
                        color = FColor.Orange_1st,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            // 顯示評論時間
            Text(
                text = review.timestamp,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            // 展開/收起回覆的按鈕
            TextButton(
                onClick = { showReplies = !showReplies },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = FColor.Orange_1st
                )
            ) {
                Text(
                    text = if (showReplies) "收起回覆" else "顯示回覆",//(${review.replies.size})",
                    fontSize = 14.sp
                )
            }

            // 按讚按鈕
            Button(
                onClick = {
                    isLiked = !isLiked
                    thumbsUpCount += if (isLiked) 1 else -1
                    if (isDisliked) {
                        isDisliked = false
                        thumbsDownCount--
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isLiked) R.drawable.baseline_thumb_up_filled
                        else R.drawable.baseline_thumb_up
                    ),
                    contentDescription = "讚",
                    modifier = Modifier.size(30.dp),
                    // 加入 tint 參數來設定顏色
                    tint = if (isLiked) FColor.Orange_1st //
                    else FColor.Dark_80 // 未選中時的顏色
                )
                Text(
                    text = " $thumbsUpCount",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // 倒讚按鈕
            Button(
                onClick = {
                    isDisliked = !isDisliked
                    thumbsDownCount += if (isDisliked) 1 else -1
                    if (isLiked) {
                        isLiked = false
                        thumbsUpCount--
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isDisliked) R.drawable.baseline_thumb_down_filled
                        else R.drawable.baseline_thumb_down
                    ),
                    contentDescription = "倒讚",
                    modifier = Modifier.size(30.dp),
                    //設定icon顏色
                    tint = if (isDisliked) FColor.Orange_1st
                    else FColor.Dark_80 // 未選中時的顏色
                )
                Text(
                    text = " $thumbsDownCount",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    // 展開時顯示回覆列表
    if (showReplies) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp)
        ) {
            review.replies.forEach { reply ->
                ReplyItem(reply)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/** 虛假的評論 */
@Composable
fun DummyReviewList(reviews: List<Reviews>) {
    var searchQuery by remember { mutableStateOf("") }

    // 為每個評論生成隨機回覆
    val reviewsWithReplies = reviews.map { review ->
        review.copy(replies = generateDummyReplies())
    }

    val filteredReviews = remember(reviewsWithReplies, searchQuery) {
        if (searchQuery.isEmpty()) {
            reviewsWithReplies
        } else {
            reviewsWithReplies.filter { review ->
                review.content.contains(searchQuery, ignoreCase = true) ||
                        review.reviewer.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = { Text("在評論中搜尋") }
        )

        filteredReviews.forEach { review ->
            DetailReviewItem(review = review)
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}


@Composable
fun ReplyItem(reply: Reply) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = FColor.Orange_1st.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reply.replier.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = FColor.Dark_80
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = reply.timestamp,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = reply.content,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DetailRevCompoentsPreview() {
    //DummyReviewList(dummyReviewList)
//    val reviewVM = remember { ReviewVM() }
//    ReviewInfoDetail(reviewVM = reviewVM)

    DetailReviewItem(review = longReview)

}