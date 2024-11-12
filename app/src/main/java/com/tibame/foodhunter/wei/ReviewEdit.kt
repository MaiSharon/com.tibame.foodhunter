package com.tibame.foodhunter.wei

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.andysearch.Restaurant
import com.tibame.foodhunter.ui.theme.FColor


@Preview
@Composable
fun PreviewReviewEdit() {
    CommentDialog(
        onDismiss = { println("Dialog Dismissed") },
        onSubmit = { comment, rating -> println("Comment Submitted: $comment with rating $rating") }
    )

}


/**評論顯示區*/
@Composable
fun ReviewZone(
    navController: NavHostController,
    viewModel: ReviewVM,
    restaurantId: Int,
    reviewId: Int? = null
) {
    /// 當 reviewId 不為 null 時才載入評論
    LaunchedEffect(reviewId) {
        reviewId?.let { id ->
            viewModel.loadReviewById(id)
        }
    }

    val context = LocalContext.current
    // 收集評論狀態
    val currentReview by viewModel.currentReview.collectAsState()

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier,
            thickness = 2.5.dp,
            color = Color(0xFFFE724C)
        )
        Spacer(modifier = Modifier.size(8.dp))

        // 顯示評論內容
        currentReview?.let { review ->
            ReviewItem(review = review)
        } ?: run {
            // 若沒有評論資料，顯示載入中或錯誤訊息
            //Text("載入中...")
        }

        Button( // 點擊後導航到 ReviewDetail頁面
            onClick = {
                // 記得傳遞餐廳 ID 參數到詳細頁面
                navController.navigate(context.getString(R.string.reviewDetail))
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

        // 確保從 viewModel 中載入評論，並傳遞正確的 restaurantId
        ReviewList(restaurantId = restaurantId, viewModel = viewModel)
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
    Log.d("ReviewList", "Loaded reviews: ${reviews.size}")

    Column{
        reviews.forEach { review ->
            ReviewItem(review)
            Spacer(modifier = Modifier.size(10.dp)) // 每筆評論間的間距
        }
    }
}

///**評論範例*/
//@Composable
//fun GetReviews() {
//    val reviews = listOf(
//        Review("使用者名稱1", 4),
//        Review("使用者名稱2", 5),
//        Review("使用者名稱3", 3),
//        Review("使用者名稱4", 2),
//        Review("使用者名稱5", 1)
//    )
//
//    LazyColumn {
//        items(reviews) { review ->
//            ReviewItem(review)
//            Spacer(modifier = Modifier.size(10.dp)) // 每筆評論間的間距
//        }
//    }
//}
/**評論範例資料*/
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
                color = FColor.Orange_1st
            )

            Spacer(modifier = Modifier.size(5.dp))

            // 顯示評分
            RatingBar(
                rating = review.rating,
                onRatingChanged = { }  //顯示用，不需改變
            )

            // 顯示評論內容
            Text(
                text = review.content,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 顯示評論時間
            Text(
                text = review.timestamp,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
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
                    tint = if (isLiked) Color(0xFFFE724C) // 橘色1st
                    else Color.Gray // 未選中時為灰色
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
                    tint = if (isDisliked) Color(0xFFFE724C) // 橘色1st
                    else Color.Gray // 未選中時為灰色
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
    onRatingChanged: (Int) -> Unit
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
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}

/**新增評論的按鈕*/
@Composable
fun CommentButton(reviewVM: ReviewVM = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            painter = painterResource(
                id = R.drawable.baseline_edit
            ),
            contentDescription = "建立評論",
            modifier = Modifier.size(30.dp)
        )
        Text("新增評論")
    }

    if (showDialog) {
        CommentDialog(
            onDismiss = { showDialog = false },
            onSubmit = { comment, rating ->
                println("評論: $comment, 評分: $rating")
                showDialog = false
            }
        )
    }
}

/**新增評論*/
@Composable
fun CommentDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit,
    reviewVM: ReviewVM = viewModel()
) {
    var commentText by remember { mutableStateOf("") }
    var inputData by remember { mutableStateOf(ReviewCreateData()) }
    var rating by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "新增評論",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                // 評論輸入框
                OutlinedTextField(
                    value = commentText,
                    onValueChange = {
                        if (it.length <= 200) {
                            commentText = it
                        }
                    },
                    label = { Text("請輸入評論 (最多200字)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    supportingText = {
                        Text("${commentText.length}/200")
                    }
                )

                // 星星評分
                RatingBar(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )

                // 按鈕列
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }

                    Button(
                        onClick = {
                            onSubmit(commentText, rating)
                            reviewVM.setReviewCreateData(inputData)
                        },
                        enabled = commentText.isNotEmpty() && rating > 0,

                        ) {
                        Text("送出")
                    }
                }
            }
        }
    }
}