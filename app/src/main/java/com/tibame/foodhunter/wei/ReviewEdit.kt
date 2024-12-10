package com.tibame.foodhunter.wei

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R


@Preview
@Composable
fun PreviewReviewEdit() {
    // 創建預覽用的必要組件
    val navController = rememberNavController()
    val previewViewModel = ReviewVM()

//    ReviewZone(
//        navController = navController,
//        viewModel = previewViewModel,
//        restaurantId = 1,
//        reviewId = 36
//    )
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