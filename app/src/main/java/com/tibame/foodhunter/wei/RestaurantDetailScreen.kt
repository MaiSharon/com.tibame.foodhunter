@file:OptIn(ExperimentalMaterial3Api::class)

package com.tibame.foodhunter.wei


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.andysearch.SearchScreenVM
import com.tibame.foodhunter.core.ui.theme.FColor
import com.tibame.foodhunter.zoe.PostViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetail(
    navController: NavHostController,
    restaurantVM: SearchScreenVM,
    reviewVM: ReviewVM,
    reviewId: Int? = null

) {

    val reviewVM: ReviewVM = viewModel()
    val postVM: PostViewModel = viewModel()
    val relatedPosts by postVM.restRelatedPosts.collectAsState()
    val reviews by reviewVM.reviewState.collectAsState()  // 收集評論列表狀態
    val restaurant by restaurantVM.choiceOneRest.collectAsState()
    val restaurantId by reviewVM.reviewState.collectAsState()
    LaunchedEffect(restaurant) { postVM.fetchRestRelatedPosts(restaurant?.restaurant_id ?: 7) }
//    Log.d(relatedPosts, )
    //val isLoading by reviewVM.isLoading.collectAsState()
    LaunchedEffect(restaurant) {
        postVM.fetchRestRelatedPosts(restaurant?.restaurant_id ?: 7)
    }
//    Log.d(relatedPosts, )

    // 根據餐廳 ID 載入評論
    LaunchedEffect(restaurantId) {
        restaurant?.restaurant_id?.let { restaurantId ->
            reviewVM.loadReviews(restaurantId)
        }
    }

    reviewId?.let { id ->
        // 當 reviewId 不為 null 時Launch
        LaunchedEffect(id) {
            reviewVM.loadReviewById(id)
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {

            RestaurantInfoDetail(navController, restaurantVM)

            RelatedPost(relatedPosts, navController)

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.5.dp,
                color = FColor.Orange_1st
            )
            Spacer(modifier = Modifier.size(4.dp))

            //評論顯示區
            Text(
                text = "評論(${reviews.size})",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp),
                color = FColor.Dark_80
            )

            // 使用安全呼叫確保傳入正確的 restaurantId
            restaurant?.restaurant_id?.let { restaurantId ->
                ReviewZone(
                    navController = navController,
                    viewModel = reviewVM,
                    restaurantId = restaurantId,  // 傳入實際的餐廳ID
                    reviewId = reviewId
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),// 給右邊的叉叉留空間
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.restaurantDetail),
                    color = Color.DarkGray,
                    fontSize = 18.sp
                )
            }
        },
        actions = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_close_24), // 使用你的叉叉圖標
                    contentDescription = "Close",
                    tint = Color.DarkGray
                )
            }
        },
    )
}


@Preview(showBackground = true)
@Composable
fun RestaurantDetailPreview() {
    // 提供一個模擬的 NavHostController 和 SearchScreenVM
    val navController = rememberNavController()
    val restaurantVM = SearchScreenVM() // 根據需要替換成模擬或預設的 ViewModel
    val reviewVM = ReviewVM()
    val reviewId: Int

    RestaurantDetail(
        navController = navController,
        restaurantVM = restaurantVM,
        reviewVM = reviewVM,
        reviewId = 0
    )
}
