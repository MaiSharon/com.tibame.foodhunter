    package com.tibame.foodhunter.wei



    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter
    import androidx.compose.ui.graphics.ImageBitmap


    data class ReviewCreateData(
        var reviewId: Int = 0,
        var reviewer: String = "",
        var rating: Int = 0,
        var content: String = "",
        var location: String = "",
        var timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
        var serviceCharge: Int = 0,
    )

    data class Reviews(
        var reviewId: Int,
        var reviewer: Reviewer,
        var restaurantId: Int,
        var rating: Int = 0,
        var content: String,
        var timestamp: String,
        var thumbsup: Int?, //從資料庫中得到的累積鑽數
        var thumbsdown: Int?,
        var isLiked: Boolean = false,
        var isDisliked: Boolean = false,
        var replies: List<Reply>,
        var maxPrice: Int,
        var minPrice: Int,
        var serviceCharge:Int = 0,
    )

    data class Reviewer(
        val id: Int,
        val name: String,
        val avatarImage: ImageBitmap? = null,
        val followers: Int = 0, // 追蹤者人數
        val following: Int = 0  // 追蹤中人數
    )



