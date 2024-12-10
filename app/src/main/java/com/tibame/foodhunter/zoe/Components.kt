package com.tibame.foodhunter.zoe

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.tibame.foodhunter.R
import com.tibame.foodhunter.andysearch.Restaurant
import com.tibame.foodhunter.core.ui.theme.FColor


sealed class ImageSource {
    data class UriSource(val uris: List<Uri>) : ImageSource()
    data class CarouselSource(val items: List<CarouselItem>) : ImageSource()
}
//兩種格式都適用
@Composable
fun ImageDisplay(
    imageSource: ImageSource,
    modifier: Modifier = Modifier
) {
    when (imageSource) {
        is ImageSource.UriSource -> {
            ImageCarouselUri(
                uris = imageSource.uris,

            )
        }
        is ImageSource.CarouselSource -> {
            ImageCarouselResource(
                items = imageSource.items,
                modifier = modifier
                    .width(394.dp)
                    .height(319.dp)
            )
        }
        else ->{}
    }
}
//從Uri拿照片
@Composable
private fun ImageCarouselUri(uris: List<Uri>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { uris.size })

    ImageCarouselLayout(
        pageCount = uris.size,
        pagerState = pagerState,
        modifier = modifier
    ) { page ->
        Image(
            painter = rememberAsyncImagePainter(uris[page]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        )
    }
}
@Composable
private fun ImageCarouselResource(items: List<CarouselItem>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    ImageCarouselLayout(
        pageCount = items.size,
        pagerState = pagerState,
        modifier = modifier
    ) { page ->
        items[page].imageData?.let { imageBitmap ->
            Image(
                bitmap = imageBitmap,
                contentDescription = "Post image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        } ?: run {
            // 如果圖片加載失敗，顯示佔位圖
            Image(
                painter = painterResource(id = R.drawable.steak_image), // 請確保有這個資源
                contentDescription = "Placeholder image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

//左右滑動的圖片顯示
@Composable
private fun ImageCarouselLayout(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            content(page)
        }

        // Only show indicators if there's more than one image
        if (pageCount > 1) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        colorResource(R.color.orange_1st)
                    } else {
                        Color.LightGray
                    }
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                            .zIndex(1f)
                    )
                }
            }
        }
    }
}
@Composable
fun ImageList(
    posts: List<Post>,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(posts) { post ->
            if (post.carouselItems.isNotEmpty()) {
                post.carouselItems.firstOrNull()?.imageData?.let { imageBitmap ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)  // 保持正方形比例
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onPostClick(post.postId) }
                    ) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Post image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } ?: run {
                    // 如果沒有圖片或圖片加載失敗，顯示佔位圖
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onPostClick(post.postId) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sushi_image_1),
                            contentDescription = "Placeholder image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun FavoriteIcon() {
    var isFavorite by remember { mutableStateOf(false) }

    IconButton(onClick = {
        isFavorite = !isFavorite
    }) {
        Icon(
            painter = painterResource(
                id = if (isFavorite) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            ),
            contentDescription = if (isFavorite) "favorite" else "not_favorite",
            modifier = Modifier.size(22.dp),
            // 加入 tint 參數來設定顏色
            tint = if (isFavorite)  FColor.Orange_1st
            else FColor.Dark_66// 未選中時為灰色
        )
    }
}



@Composable
fun RestaurantList(
    restaurants: List<Restaurant>,
    onRestaurantSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(restaurants) { restaurant ->
            RestaurantItem(
                restaurant = restaurant,
                onClick = { onRestaurantSelected(restaurant.restaurant_id) }  // 轉換成字串
            )
        }
    }
}


@Composable
private fun RestaurantItem(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = FColor.Orange_3rd,  // 卡片背景顏色
            contentColor = FColor.Dark_80  // 卡片內容顏色
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Select",
                tint = FColor.Dark_66
            )
        }
    }
}

@Composable
fun Avatar(
    imageData: ImageBitmap?, // 自定義圖片
    defaultImage: Int = R.drawable.user1, // 預設圖片資源
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null, // 可選的點擊事件
    contentDescription: String? = null
) {
    val modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .then(
            if (onClick != null) {
                Modifier.clickable(onClick = onClick)
            } else {
                Modifier
            }
        )

    when {
        imageData != null -> {
            Image(
                bitmap = imageData,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        else -> {
            Image(
                painter = painterResource(id = defaultImage),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
    }
}
