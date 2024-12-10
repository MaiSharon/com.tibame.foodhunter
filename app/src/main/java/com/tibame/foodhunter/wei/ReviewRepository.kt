package com.tibame.foodhunter.wei

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReviewRepository {


    private val _reviewList = MutableStateFlow<List<Reviews>>(emptyList())
    val reviewList: StateFlow<List<Reviews>> = _reviewList.asStateFlow()

    private val _replyList = MutableStateFlow<List<Reply>>(emptyList())
    val replyList: StateFlow<List<Reply>> = _replyList.asStateFlow()

    private val gson = Gson()

    // 建立自定義的 Gson 實例來處理日期格式
    private val gsonDate: Gson = Gson().newBuilder()
        .setDateFormat("MMM dd, yyyy, hh:mm:ss a") //根據服務器返回的日期格式設置
        .create()

    // 根據評論 ID 獲取該評論的回覆列表
    private suspend fun fetchReplies(reviewId: Int): List<ReplyResponse> {
        val url = "$serverUrl/reply/byReview?reviewId=$reviewId"
        val result = CommonPost(url, "")
        val type = object : TypeToken<List<ReplyResponse>>() {}.type
        return try {
            gson.fromJson(result, type)
        } catch (e: Exception) {
            Log.e("ReviewRepository", "Error fetching replies", e)
            emptyList()
        }
    }

    // 將回覆轉換為應用程式內的 Reply 物件
    private suspend fun ReplyResponse.toReply(): Reply {
        return Reply(
            id = this.replyId,
            replier = Replier(
                id = this.memberId,
                name = this.memberNickname,
                avatarImage = null // 若有頭像處理方式，將其放入這裡
            ),
            content = this.content,
            timestamp = this.replyTime
        )
    }

    // 根據餐廳ID獲取評論列表
    suspend fun fetchReviewByRestId(restaurantId: Int): List<ReviewResponse?> {
        val url = "$serverUrl/review/preLoadController"
        Log.d("ReviewRepository", "Fetching reviews for restaurant ID: $restaurantId")
        Log.d("ReviewRepository", "Request URL: $url")

        val jsonObject = JsonObject()
        jsonObject.addProperty("restaurantId", restaurantId)

        return try {
            val result = CommonPost(url, jsonObject.toString())
            Log.d("ReviewRepository", "Received response: $result")

            if (result.isNullOrEmpty()) {
                Log.d("ReviewRepository", "Empty response from server")
                return emptyList()
            }

            // 使用自定義的 TypeToken 來處理日期格式
            val type = object : TypeToken<List<ReviewResponse>>() {}.type
            val reviews = gsonDate.fromJson<List<ReviewResponse>>(result, type)

            Log.d("Repository", "Parsed reviews: ${reviews.size}")
            reviews.forEach { review ->
                Log.d("Repository", "Review: $review")
            }

            reviews
        } catch (e: Exception) {
            Log.e("ReviewRepository", "Error fetching reviews for restaurant $restaurantId", e)
            emptyList()
        }

    }


    // 根據評論ID 獲取評論的詳細資料
    suspend fun fetchReviewById(reviewId: Int): ReviewResponse? {
        val url = "$serverUrl/review/getReviewById"

        try {
            val jsonObject = JsonObject().apply {
                addProperty("reviewId", reviewId)
            }
            val result = CommonPost(url, jsonObject.toString())
            Log.d("ReviewRepository", "Fetching review ID: $reviewId")
            Log.d("ReviewRepository", "Response: $result")

            // 檢查回應是否為空
            if (result.isNullOrEmpty()) {
                Log.e("ReviewRepository", "Empty response from server")
                return null
            }
            // 解析回應
            return try {
                val response = gson.fromJson(result, ApiResponse::class.java)
                if (response?.success == true) {
                    gson.fromJson(gson.toJson(response.data), ReviewResponse::class.java)
                } else {
                    Log.e("ReviewRepository", "Error fetching review: ${response?.message}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ReviewRepository", "Error parsing response", e)
                null
            }
        } catch (e: Exception) {
            Log.e("ReviewRepository", "Error fetching review by ID", e)
            return null
        }
    }

    // 創建新回覆
    suspend fun createReply(reviewId: Int, userId: Int, content: String): Boolean {
        val url = "$serverUrl/reply/create"
        val replyRequest = mapOf(
            "reviewId" to reviewId,
            "memberId" to userId,
            "content" to content
        )

        return try {
            val json = gson.toJson(replyRequest)
            val result = CommonPost(url, json)
            loadReplies(reviewId) // 重新載入回覆列表
            true
        } catch (e: Exception) {
            Log.e("ReviewRepository", "Error creating reply", e)
            false
        }
    }


 //載入所有回覆
    suspend fun loadReplies(reviewId: Int) {
        try {
            val replyResponses = fetchReplies(reviewId)
            val replies = replyResponses.mapNotNull { response ->
                try {
                    response.toReply()
                } catch (e: Exception) {
                    null
                }
            }

            // 更新回覆列表
            _replyList.update { replies }
        } catch (e: Exception) {
            Log.e("ReviewRepository", "Error loading replies", e)
        }
    }

//    同時載入回覆及評論
//    suspend fun loadReviewsWithReplies(restaurantId: Int): List<Review> {
//        // 1. 獲取餐廳的評論列表
//        val reviews = fetchReviewByRestId(restaurantId).mapNotNull { it?.toReview() }
//
//        // 2. 並行載入每則評論的回覆
//        val reviewsWithReplies = reviews.map { review ->
//            val replies = fetchReplies(review.reviewId).map { it.toReply() }
//            review.copy(replies = replies)  // 更新評論物件中的回覆列表
//        }
//
//        // 更新 _reviewList 狀態，供 ViewModel 監聽使用
//        _reviewList.update { reviewsWithReplies }
//
//        return reviewsWithReplies
//    }


//    // 刪除回覆
//    suspend fun deleteReply(replyId: Int): Boolean {
//        val url = "${serverUrl}/reply/delete/$replyId"
//        return try {
//            val result = CommonPost(url, "")
//            val response = gson.fromJson(result, DeleteResponse::class.java)
//            if (response.success) {
//                // 刪除成功後更新本地回覆列表
//                _reviewList.value = _reviewList.value.filter { it.id != replyId }
//                true
//            } else {
//                Log.e("ReviewRepository", "Delete failed: ${response.message}")
//                false
//            }
//        } catch (e: Exception) {
//            Log.e("ReviewRepository", "Error deleting reply", e)
//            false
//        }
//    }

    companion object {
        @Volatile
        private var INSTANCE: ReviewRepository? = null

        fun getInstance(): ReviewRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReviewRepository().also { INSTANCE = it }
            }
        }
    }

    // 數據類別
    data class ReviewResponse(
        val reviewId: Int,
        val reviewer: Int,         // 對應資料庫的 reviewer
        val restaurantId: Int,
        val rating: Int,
        val comments: String,      // 對應資料庫的評論內容
        val reviewDate: String,
        val thumbsUp: Int,
        val thumbsDown: Int,
        val priceRangeMax: Int,
        val priceRangeMin: Int,
        val serviceCharge: Int,
        val reviewerNickname: String,
        val restaurantName: String
    )


    data class ReplyResponse(
        val replyId: Int,
        val reviewId: Int,
        val memberId: Int,
        val content: String,
        val replyTime: String,
        val memberNickname: String
    )

    data class ApiResponse<T>(
        val success: Boolean,
        val message: String,
        val data: T
    )

//    data class ReviewResponse(
//        val reviewId: Int,
//        val content: String,
//        val rating: Int,
//        val reviewTime: String,
//        val userId: Int,
//        val userNickname: String
//    ) {
//        fun toReview(): Review {
//            return Review(
//                reviewId = reviewId,
//                content = content,
//                rating = rating,
//                timestamp = reviewTime,
//                reviewer = Reviewer(userId, userNickname, avatarImage = null),
//                replies = emptyList() // 回覆列表稍後加載
//            )
//        }
//    }
//
//    data class ReplyResponse(
//        val replyId: Int,
//        val reviewId: Int,
//        val memberId: Int,
//        val content: String,
//        val replyTime: String,
//        val memberNickname: String
//    ) {
//        fun toReply(): Reply {
//            return Reply(
//                id = replyId,
//                content = content,
//                timestamp = replyTime,
//                replier = Replier(memberId, memberNickname, avatarImage = null)
//            )
//        }
//    }

//    data class DeleteResponse(
//        val success: Boolean,
//        val message: String)
}

//抓會員ID用
//val membernameState = remember { mutableStateOf(0) }
//LaunchedEffect(Unit) {
//    coroutineScope.launch {
//        val user = userVM.getUserInfo(userVM.username.value)
//        //圖片
//    val user1 = userVM.image(userVM.username.value)}
//        if (user != null) {
//            membernameState.value = user.id
//            //圖片
//            user1?.profileImageBase64?.let { base64 ->
//                profileBitmap = userVM.decodeBase64ToBitmap(base64)
//        }
//    }
//}