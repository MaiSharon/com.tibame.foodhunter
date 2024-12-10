package com.tibame.foodhunter.wei

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.andysearch.SearchScreenVM
import com.tibame.foodhunter.core.ui.theme.FColor

@Preview
@Composable
fun ReviewCompoentsPreview() {
    val navController = rememberNavController()
    val restaurantVM = SearchScreenVM() // 根據需要替換成模擬或預設的 ViewModel
    val reviewVM = ReviewVM()

    ReviewInfoDetail(reviewVM = reviewVM)
}

/**評論顯示區*/
@Composable
fun ReviewZone(
    navController: NavHostController,
    viewModel: ReviewVM,
    restaurantId: Int,
    reviewId: Int? = null
) {
    val context = LocalContext.current
    // 收集評論狀態
    val currentReview by viewModel.currentReview.collectAsState()

    // 當 reviewId 不為 null 時才載入評論
    LaunchedEffect(reviewId) {
        if (reviewId != null && reviewId > 0) {
            viewModel.loadReviewById(reviewId)
        }
    }

    HorizontalDivider(
        modifier = Modifier,
        thickness = 1.5.dp,
        color = Color(0xFFFE724C)
    )

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    ) {

        // 顯示評論內容
        currentReview?.let { review ->
            ReviewItem(review = review)
        }

        Button( // 點擊後導航到 ReviewDetail頁面
            onClick = {
                // 記得傳遞餐廳 ID 參數到詳細頁面
                navController.navigate(context.getString(R.string.reviewDetail))
                Log.d("ReviewDetail", "Navigate reviews for restaurantId: $restaurantId")
            },
            modifier = Modifier
                .width(150.dp)
                .height(50.dp),
            shape = ButtonDefaults.outlinedShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFED2C7),
                contentColor = Color(0xFFB43310)
            )
        ) {
            Text(
                text = "顯示所有評論",
                modifier = Modifier,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.size(8.dp))

        // 顯示評論列表
        if (restaurantId > 0) {
            ReviewList(restaurantId = restaurantId, viewModel = viewModel)
        }
    }
}

/** 真正的評論 */
@Composable
fun ReviewList(restaurantId: Int, viewModel: ReviewVM) {
    // 當頁面載入時，載入該餐廳的評論
    LaunchedEffect(restaurantId) {
        Log.d("ReviewList", "Loading reviews for restaurantId: $restaurantId")
        viewModel.loadReviews(restaurantId)
    }

    val reviews by viewModel.reviewState.collectAsState()  // 觀察評論列表的資料變動
    val isLoading by viewModel.isLoading.collectAsState()
    Log.d("ReviewList", "Loaded reviews: ${reviews.size}")

    Column {
        if (isLoading) {
            // 添加載入指示器
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                color = FColor.Orange_1st
            )
        } else {
            if (reviews.isEmpty()) {
                Text(
                    text = "目前尚無評論",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                reviews.forEach { review ->
                    ReviewItem(review)
                    Spacer(modifier = Modifier.size(10.dp)) // 每筆評論間的間距
                }
            }
        }
    }
}

/**評論資料*/
@Composable
fun ReviewItem(review: Reviews?) {
    var isLiked by remember { mutableStateOf(review?.isLiked ?: false) }
    var isDisliked by remember { mutableStateOf(review?.isDisliked ?: false) }
    var thumbsUpCount by remember { mutableStateOf(review?.thumbsup ?: 0) }
    var thumbsDownCount by remember { mutableStateOf(review?.thumbsdown ?: 0) }
    if (review == null) {
        Text(
            text = "目前尚無評論",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        return
    }

    Row {

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(1f)
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

            // 顯示評論內容
//            Text(
//                text = review.content,
//                fontSize = 16.sp,
//                color = Color.Black,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )

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
                Text(text = " $thumbsUpCount",
                    modifier = Modifier.padding(start = 8.dp))
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
                    // 加入 tint 參數來設定顏色
                    tint = if (isDisliked) FColor.Orange_1st
                    else FColor.Dark_80 // 未選中時的顏色
                )
                Text(text = " $thumbsDownCount",
                    modifier = Modifier.padding(start = 8.dp))
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier,
        thickness = 0.5.dp,
        color = FColor.Orange_1st
    )
}


/**星星*/
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    filledStarColor: Color = FColor.Yellow_1,
    outlineStarColor: Color = FColor.Dark_80
) {


    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(
                    id = if (i <= rating) R.drawable.baseline_star else R.drawable.baseline_star_outline
                ),
                contentDescription = "$i 顆星",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { onRatingChanged(i) },
                tint = if (i <= rating) filledStarColor else outlineStarColor
            )
        }
    }
}





/**詳細評論資料*/
//@Composable
//fun ReviewItem(review: Reviews?) {
//    var isLiked by remember { mutableStateOf(review?.isLiked ?: false) }
//    var isDisliked by remember { mutableStateOf(review?.isDisliked ?: false) }
//    var thumbsUpCount by remember { mutableStateOf(review?.thumbsup ?: 0) }
//    var thumbsDownCount by remember { mutableStateOf(review?.thumbsdown ?: 0) }
//    if (review == null) {
//        Text(
//            text = "目前尚無評論",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black
//        )
//        return
//    }
//
//    Row {
//
//        Column(
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Top,
//            modifier = Modifier.weight(1f)
//        ) {
//            Spacer(modifier = Modifier.size(8.dp))
//
//            // 顯示評論者名稱
//            Text(
//                text = review.reviewer.name,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = FColor.Dark_80
//            )
//
//            Spacer(modifier = Modifier.size(5.dp))
//
//            // 顯示評分
//            RatingBar(
//                rating = review.rating,
//                onRatingChanged = { }  //顯示用，不需改變
//            )
//
//            // 顯示評論內容
//            Text(
//                text = review.content,
//                fontSize = 16.sp,
//                color = Color.Black,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//
//            // 顯示評論時間
//            Text(
//                text = review.timestamp,
//                fontSize = 14.sp,
//                color = Color.Gray
//            )
//        }
//
//        Column(
//            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Bottom,
//            modifier = Modifier.padding(top = 32.dp)
//        ) {
//            // 按讚按鈕
//            Button(
//                onClick = {
//                    isLiked = !isLiked
//                    thumbsUpCount += if (isLiked) 1 else -1
//                    if (isDisliked) {
//                        isDisliked = false
//                        thumbsDownCount--
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Transparent,
//                    contentColor = Color.Black
//                )
//            ) {
//                Icon(
//                    painter = painterResource(
//                        id = if (isLiked) R.drawable.baseline_thumb_up_filled
//                        else R.drawable.baseline_thumb_up
//                    ),
//                    contentDescription = "讚",
//                    modifier = Modifier.size(30.dp),
//                    // 加入 tint 參數來設定顏色
//                    tint = if (isLiked) FColor.Orange_1st //
//                    else FColor.Dark_80 // 未選中時的顏色
//                )
//                Text(text = " $thumbsUpCount",
//                    modifier = Modifier.padding(start = 8.dp))
//            }
//
//            // 倒讚按鈕
//            Button(
//                onClick = {
//                    isDisliked = !isDisliked
//                    thumbsDownCount += if (isDisliked) 1 else -1
//                    if (isLiked) {
//                        isLiked = false
//                        thumbsUpCount--
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Transparent,
//                    contentColor = Color.Black
//                )
//            ) {
//                Icon(
//                    painter = painterResource(
//                        id = if (isDisliked) R.drawable.baseline_thumb_down_filled
//                        else R.drawable.baseline_thumb_down
//                    ),
//                    contentDescription = "倒讚",
//                    modifier = Modifier.size(30.dp),
//                    // 加入 tint 參數來設定顏色
//                    tint = if (isDisliked) FColor.Orange_1st
//                    else FColor.Dark_80 // 未選中時的顏色
//                )
//                Text(text = " $thumbsDownCount",
//                    modifier = Modifier.padding(start = 8.dp))
//            }
//        }
//    }
//
//    HorizontalDivider(
//        modifier = Modifier,
//        thickness = 0.5.dp,
//        color = FColor.Orange_1st
//    )
//}