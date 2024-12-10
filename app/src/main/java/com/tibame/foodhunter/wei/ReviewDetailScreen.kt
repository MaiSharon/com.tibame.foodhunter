package com.tibame.foodhunter.wei

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.andysearch.SearchScreenVM
import com.tibame.foodhunter.core.ui.theme.FColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetail(
    navController: NavHostController,
    reviewVM: ReviewVM = remember { ReviewVM() },
    restaurantVM: SearchScreenVM,
    reviewId: Int? = null
) {
    val reviews by reviewVM.reviewState.collectAsState()  // 收集評論列表狀態
    val restaurant by restaurantVM.choiceOneRest.collectAsState()
    val restaurantId by reviewVM.reviewState.collectAsState()

    // 確保從餐廳獲取正確的 ID
    LaunchedEffect(restaurant) {
        restaurant?.restaurant_id?.let { id ->
            reviewVM.loadReviews(id)
        }
    }
    // 如果有特定評論 ID，載入該評論
    LaunchedEffect(reviewId) {
        if (reviewId != null && reviewId != 0) {
            reviewVM.loadReviewById(reviewId)
        }
    }

    LaunchedEffect(reviews) {
        Log.d("RestaurantDetail", "Current reviews count: ${reviews.size}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.5.dp,
            color = FColor.Orange_1st
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            //items(1) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.5.dp,
                    color = FColor.Orange_1st
                )
            //}

            //items(1) {
                ReviewInfoDetail(reviewVM = reviewVM)
            //}

            //items(1) {
                restaurant?.restaurant_id?.let { restaurantId ->
                    DetailReviewZone(
                        navController = navController,
                        viewModel = reviewVM,
                        restaurantId = restaurantId
                    )
                } ?: run {}
                // 使用假資料
                //DummyReviewList(dummyReviewList)
            //    }

            //items(1) {

                HorizontalDivider(
                    modifier = Modifier,
                    thickness = 1.dp,
                    color = FColor.Orange_1st
                )
            //}

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewDetailScreenPreview() {
    val navController =
        rememberNavController()  // 使用 rememberNavController() 來建立一個模擬的 NavController
    val reviewVM = remember { ReviewVM() }        // 使用模擬的 ReviewVM
    val restaurantVM = remember { SearchScreenVM() } // 使用模擬的 SearchScreenVM

    ReviewDetail(
        navController = navController,
        reviewVM = reviewVM,
        restaurantVM = restaurantVM,
    )
}