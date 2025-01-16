package com.tibame.foodhunter.wei

import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DummyDataRepository {
    private const val isMockMode = true // 控制是否使用假資料
    private const val serverUrl = "http://10.0.2.2:8080/com.tibame.foodhunter_server/"
    private val gson = Gson()

    // 使用假資料並為每個評論生成回覆
    private val mockReviews = dummyReviewList.map { review ->
        review.copy(replies = generateDummyReplies()) //
    }

    // 用於假資料或 API 資料的 MutableStateFlow
    private val _reviewList = MutableStateFlow<List<Reviews>>(emptyList())
    val reviewList: StateFlow<List<Reviews>> = _reviewList

    init {
        // 根據 isMockMode 的設定載入假資料
        if (isMockMode) {
            _reviewList.value = mockReviews
        } else {
            // 這裡可以添加從伺服器獲取資料的邏輯
            loadReviewsFromServer()
        }
    }

    // 從伺服器獲取資料的假設方法（實際 API 調用的實現）
    private fun loadReviewsFromServer() {
        // 使用伺服器 API 獲取資料並更新 _reviewList
        // 假設此函數包含網路請求邏輯
    }
}

val dummyReviewList = listOf(
    Reviews(
        reviewId = 1,
        reviewer = Reviewer(id = 1, name = "A冷"),
        restaurantId = 101,
        rating = 5,
        content = "食物超級美味，服務也很好，讓我非常滿意！",
        timestamp = "2024/11/12",
        thumbsup = 42,
        thumbsdown = 2,
        isLiked = true,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 800,
        minPrice = 300,
        serviceCharge = 10
    ),
    Reviews(
        reviewId = 2,
        reviewer = Reviewer(id = 2, name = "B冷"),
        restaurantId = 101,
        rating = 4,
        content = "整體體驗不錯，但等位時間稍微長了一些。",
        timestamp = "2024/11/11",
        thumbsup = 34,
        thumbsdown = 4,
        isLiked = true,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 700,
        minPrice = 250,
        serviceCharge = 10
    ),
    Reviews(
        reviewId = 3,
        reviewer = Reviewer(id = 3, name = "C冷"),
        restaurantId = 101,
        rating = 3,
        content = "餐點味道一般，服務態度普通。",
        timestamp = "2024/11/10",
        thumbsup = 12,
        thumbsdown = 10,
        isLiked = false,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 600,
        minPrice = 200,
        serviceCharge = 5
    ),
    Reviews(
        reviewId = 4,
        reviewer = Reviewer(id = 4, name = "A咪"),
        restaurantId = 101,
        rating = 2,
        content = "餐廳的環境很吵鬧，無法好好享受食物。",
        timestamp = "2024/11/09",
        thumbsup = 8,
        thumbsdown = 16,
        isLiked = false,
        isDisliked = true,
        replies = listOf(),
        maxPrice = 500,
        minPrice = 200,
        serviceCharge = 5
    ),
    Reviews(
        reviewId = 5,
        reviewer = Reviewer(id = 5, name = "B咪"),
        restaurantId = 101,
        rating = 5,
        content = "無可挑剔的用餐體驗，強烈推薦！",
        timestamp = "2024/11/08",
        thumbsup = 51,
        thumbsdown = 1,
        isLiked = true,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 1000,
        minPrice = 350,
        serviceCharge = 15
    ),
    Reviews(
        reviewId = 6,
        reviewer = Reviewer(id = 6, name = "C咪"),
        restaurantId = 101,
        rating = 4,
        content = "食物很好吃，服務也很周到，性價比高。",
        timestamp = "2024/11/07",
        thumbsup = 38,
        thumbsdown = 3,
        isLiked = true,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 900,
        minPrice = 300,
        serviceCharge = 10
    ),
    Reviews(
        reviewId = 7,
        reviewer = Reviewer(id = 7, name = "A冷"),
        restaurantId = 101,
        rating = 3,
        content = "餐廳裝潢很好，但餐點出餐時間慢了一點。",
        timestamp = "2024/11/06",
        thumbsup = 20,
        thumbsdown = 5,
        isLiked = false,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 650,
        minPrice = 220,
        serviceCharge = 5
    ),
    Reviews(
        reviewId = 8,
        reviewer = Reviewer(id = 8, name = "B冷"),
        restaurantId = 101,
        rating = 2,
        content = "價格偏高，食物品質不符合期待。",
        timestamp = "2024/11/05",
        thumbsup = 6,
        thumbsdown = 12,
        isLiked = false,
        isDisliked = true,
        replies = listOf(),
        maxPrice = 700,
        minPrice = 300,
        serviceCharge = 10
    ),
    Reviews(
        reviewId = 9,
        reviewer = Reviewer(id = 9, name = "C冷"),
        restaurantId = 101,
        rating = 4,
        content = "服務態度親切，餐點美味，會再來訪。",
        timestamp = "2024/11/04",
        thumbsup = 35,
        thumbsdown = 4,
        isLiked = true,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 850,
        minPrice = 300,
        serviceCharge = 10
    ),
    Reviews(
        reviewId = 10,
        reviewer = Reviewer(id = 10, name = "A咪"),
        restaurantId = 101,
        rating = 5,
        content = "非常愉快的用餐體驗，讓人感到賓至如歸。",
        timestamp = "2024/11/03",
        thumbsup = 48,
        thumbsdown = 1,
        isLiked = true,
        isDisliked = false,
        replies = listOf(),
        maxPrice = 950,
        minPrice = 400,
        serviceCharge = 15
    )
)
//超長評論測試
val longReview = Reviews(
    reviewId = 1,
    reviewer = Reviewer(id = 1, name = "測試用戶"),
    restaurantId = 101,
    rating = 4,
    content = "這是一個很長的評論內容。\n第二行內容。\n第三行內容。\n第四行內容。\n第五行內容。",
    timestamp = "2024/11/12",
    thumbsup = 10,
    thumbsdown = 2,
    isLiked = false,
    isDisliked = false,
    replies = listOf(),
    maxPrice = 800,
    minPrice = 300,
    serviceCharge = 10
)

// 假資料生成函數
fun generateDummyReplies(): List<Reply> {
    val replyContents = listOf(
        "餐點真的很美味，特別是他們的招牌料理！",
        "服務態度非常好，氣氛也很棒。",
        "價格稍微高了一點，但是品質絕對對得起這個價位。",
        "環境很乾淨，座位也很舒適。",
        "等待時間有點長，但食物的品質值得等待。",
        "會推薦給朋友來試試！",
        "讚哦~",
        "在我家附近，改天來試試!",
        "上次去等超久。",
        "假日建議訂位",
        "上餐速度很快!"
    )

    val replierNames = listOf("A咪", "B冷", "C咪", "A冷", "B咪", "G咪", "哈哈", "煞氣卍小鬼")

    // 生成1-9條隨機回覆
    val numberOfReplies = Random.nextInt(1, 10)

    // 建立足夠大的索引範圍來對應內容列表
    val contentIndices = (replyContents.indices).shuffled().take(numberOfReplies)
    val nameIndices = (replierNames.indices).shuffled().take(numberOfReplies)

    return contentIndices.mapIndexed { index, contentIndex ->
        Reply(
            id = index + 1,
            replier = Replier(
                id = index + 1,
                name = replierNames[nameIndices[index % replierNames.size]], // 使用模運算確保不會超出範圍
                avatarImage = null
            ),
            content = replyContents[contentIndex],
            timestamp = LocalDateTime.now()
                .minusDays(Random.nextLong(1, 30))
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
        )
    }
}


fun generateDummyRepliess(): List<Reply> {
    val replyContents = listOf(
        "餐點真的很美味，特別是他們的招牌料理！",
        "服務態度非常好，氣氛也很棒。",
        "價格稍微高了一點，但是品質絕對對得起這個價位。",
        "環境很乾淨，座位也很舒適。",
        "等待時間有點長，但食物的品質值得等待。"
    )

    val replierNames = listOf("G咪", "B冷", "C咪", "A冷", "B咪")

    return List(5) { index ->
        Reply(
            id = index + 1,
            replier = Replier(
                id = index + 1,
                name = replierNames[index],
                avatarImage = null
            ),
            content = replyContents[index],
            timestamp = LocalDateTime.now()
                .minusDays(Random.nextLong(1, 30))
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
        )
    }
}
