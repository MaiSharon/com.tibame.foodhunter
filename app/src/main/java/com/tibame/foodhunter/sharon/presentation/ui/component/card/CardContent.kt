package com.tibame.foodhunter.sharon.presentation.ui.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.tibame.foodhunter.sharon.domain.entity.CardContentType


@Preview
@Composable
fun CardContentPreview(){

}

fun String.limitChineseLength(maxLength: Int = 15): String {
    return if (this.length > maxLength) {
        "${this.take(maxLength)}..."
    } else {
        this
    }
}

@Composable
fun CardContent(
    type: CardContentType,
    color: Color,
    date: String,
    day: String,
    circleColor: Color,
    title: String,
    modifier: Modifier = Modifier,
    // Group 特有參數
    restaurantName: String? = null,
    restaurantAddress: String? = null,
//    headcount: Int? = null,
    isPublic: Int? = null,
    // Note 特有參數
    content: String? = null,
    imageResId: Int? = null
) {
    Row(modifier = modifier.fillMaxSize()) {
        ColorBox(color = color)

        DateBox(
            date = date,
            day = day,
            color = circleColor,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        when (type) {
            CardContentType.GROUP -> {
                GroupTextContent(
                    title = title.limitChineseLength(10),
                    restaurantName = restaurantName?.limitChineseLength() ?: "",
                    restaurantAddress = restaurantAddress?.limitChineseLength(14) ?: "",
//                    headcount = headcount ?: 3,
                    )

                // 揪團才需要顯示公開/私密切換
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){ GroupVisibilityToggle(isPublic = isPublic) }
            }
            CardContentType.NOTE -> {
                NoteTextContent(
                    title = title.limitChineseLength(),
                    content = content?.limitChineseLength() ?: "",
                    modifier = Modifier.weight(1f)
                )
                imageResId?.let {
                    NoteImage(imageResId = it)
                }
            }
        }
    }
}

