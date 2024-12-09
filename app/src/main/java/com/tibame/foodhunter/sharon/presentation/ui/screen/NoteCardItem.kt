package com.tibame.foodhunter.sharon.presentation.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.components.card.ColorBox
import com.tibame.foodhunter.sharon.components.card.DateBox
import com.tibame.foodhunter.sharon.components.card.NoteImage
import com.tibame.foodhunter.sharon.components.card.NoteTextContent

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    NoteCardItem(
        imageResId = R.drawable.sushi_image_1,
        title = "小巷中的咖啡廳"
    )// 預覽時顯示的圖片資源 ID
}

@Composable
fun NoteCardItem(
    navController: NavHostController = rememberNavController(),
    imageResId: Int? = null,
    title: String
) {
    Card(
        modifier = Modifier
            .padding(3.dp)
            .width(380.dp)
            .height(88.dp)
            .border(width = 2.dp, color = Color(0xFFEBEBEB), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 黃色:筆記、橘色:揪團
            ColorBox(color = colorResource(id = R.color.yellow_1))
            // 日期 跟 底圖顏色
            DateBox(
                date = "10/17",
                day = "星期四",
                color = Color(0xFFFFEFC3),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            // 標題、內文。傳遞 .weight(1f) 保持填補Row空間
            NoteTextContent(
                title = title,
                content = "這裡有各種美味的咖啡和小吃、環境乾淨...",
                modifier = Modifier.weight(1f)
            )
            // 判斷是否有圖片資源 ID，如果有則顯示圖片，否則跳過此步驟
            imageResId?.let {
                NoteImage(imageResId = it)
            }
        }
    }
}











