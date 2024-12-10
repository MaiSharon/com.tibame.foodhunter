package com.tibame.foodhunter.sharon.presentation.ui.component.card

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.domain.entity.CardContentType

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    GroupCardExample()
    NoteCardExample()
}

@Composable
fun NoteOrGroupCard(
    type: CardContentType,
    date: String,
    day: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    // Group 特有參數
    restaurantName: String? = null,
    restaurantAddress: String? = null,
//    headcount: Int? = null,
    isPublic: Int? = null,
    // Note 特有參數
    content: String? = null,
    imageResId: Int? = null
) {
    // 邊邊的顏色
    val boxColor = when(type) {
        CardContentType.GROUP -> colorResource(id = R.color.orange_1st)
        CardContentType.NOTE -> colorResource(id = R.color.yellow_1)
    }

    // 圓形背景顏色
    val circleColor = when(type) {
        CardContentType.GROUP -> colorResource(id = R.color.orange_5th)
        CardContentType.NOTE -> colorResource(id = R.color.yellow_20)
    }


    Card(
        modifier = modifier
            .padding(3.dp)
            .width(380.dp)
            .height(88.dp)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = Color(0xFFEBEBEB),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        CardContent(
            type = type,
            color = boxColor,
            date = date,
            day = day,
            circleColor = circleColor,
            title = title,
            restaurantName = restaurantName,
            restaurantAddress = restaurantAddress,
//            headcount = headcount,
            isPublic = isPublic,
            content = content,
            imageResId = imageResId
        )
    }
}

// 揪團
@Composable
fun GroupCardExample() {
    NoteOrGroupCard(
        onClick = {},
        type = CardContentType.GROUP,
        date = "10/17",
        day = "星期四",
        title = "說好的減肥呢",
        restaurantName = "麥噹噹",
        restaurantAddress = "台北市中山區南京東路三段222號",
//        headcount = 4,
        isPublic = 0
    )
}

// 筆記
@Composable
fun NoteCardExample() {
    NoteOrGroupCard(
        onClick = {},
        type = CardContentType.NOTE,
        date = "10/17",
        day = "星期四",
        title = "小巷中的咖啡廳",
        content = "這裡有各種美味的咖啡和小吃、環境乾淨...",
        imageResId = R.drawable.sushi_image_1
    )
}