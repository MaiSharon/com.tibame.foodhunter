package com.tibame.foodhunter.sharon.domain.entity

import java.util.Date


data class Note(
    val type: CardContentType,
    val title: String,
    val noteId: Int,
    val selectedDate: Date = Date(),
    val date: String,
    val day: String,
    val content: String,
    val imageResId: Int? = null, // 可選欄位
    val restaurantName: String? = null, // 可選欄位
    val memberId: Int
)

data class Group(
    val groupId: Int,
    val groupName: String,
    val restaurantName: String? = null,
    val restaurantAddress: String? = null,
    val isPublic: Int,
    val groupDate: Date = Date(),
    val type: CardContentType,
    val date: String,        // MM/dd 格式
    val day: String,         // 星期幾
    val memberId: Int
)

// 定義內容類型的枚舉
enum class CardContentType {
    GROUP,  // 揪團
    NOTE    // 筆記
}
