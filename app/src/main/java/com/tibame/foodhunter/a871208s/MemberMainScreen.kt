package com.tibame.foodhunter.a871208s

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.sharon.presentation.viewmodel.PersonalToolsVM
import kotlinx.coroutines.launch

@Composable
fun MemberMainScreen(
    navController: NavHostController = rememberNavController(), userVM: UserViewModel, personalToolsVM: PersonalToolsVM = viewModel()
) {
    val context = LocalContext.current
    val usernameState = remember { mutableStateOf("") }
    val nicknameState = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val currentUserId by userVM.memberId.collectAsState()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = userVM.getUserInfo(userVM.username.value)
            //圖片
            val user1 = userVM.image(userVM.username.value)
            if (user != null) {
                usernameState.value = user.username // 獲取用戶名
                nicknameState.value = user.nickname
                //圖片
                user1?.profileImageBase64?.let { base64 ->
                    profileBitmap = userVM.decodeBase64ToBitmap(base64)
                }
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        //color = ,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp, 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.Center,

            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(colorResource(R.color.orange_5th), shape = RoundedCornerShape(12.dp))
            /*
                            .shadow(
                                elevation = 2.dp, // 設置陰影的高度，8.dp 也可以根據需要調整
                                shape = RoundedCornerShape(12.dp), // 使用相同的圓角形狀
                                spotColor = Color.LightGray, // 陰影的顏色（可以根據需要調整）
                                ambientColor = Color.LightGray, // 陰影的環境顏色
                                clip = true // 不裁剪陰影（如果你希望陰影被裁剪，可以設置為 true）
                            )*/
        ) {
            Row {
                Column(
                    modifier = Modifier.size(150.dp, 180.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap!!.asImageBitmap(),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(120.dp)
                                .border(BorderStroke(10.dp, Color.White), CircleShape)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // 顯示占位符圖片
                        Image(
                            painter = painterResource(id = R.drawable.account_circle),
                            contentDescription = "Default Image",
                            modifier = Modifier
                                .size(120.dp)
                                .border(BorderStroke(10.dp, Color.White), CircleShape)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(
                    modifier = Modifier.size(150.dp, 180.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "帳號",
                        fontSize = 16.sp,
                        color = Color.Gray,
                    )
                    Text(
                        text = usernameState.value,
                        fontSize = 20.sp,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = "暱稱",
                        fontSize = 16.sp,
                        color = Color.Gray,
                    )
                    Text(
                        text = nicknameState.value,
                        fontSize = 20.sp,
                        color = Color.Black,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.navigate(context.getString(R.string.str_member) + "/2") }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )

                Text(
                    text = "會員資料",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.navigate(context.getString(R.string.str_member) + "/6") }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )
                Text(
                    text = "好友管理",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.navigate(context.getString(R.string.str_note)) }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )
                Text(
                    text = "我的手札",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.navigate(context.getString(R.string.str_calendar)) }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )
                Text(
                    text = "我的日曆",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp),

            ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.navigate("person_homepage/$currentUserId") }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )
                Text(
                    text = "我的貼文",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.navigate(route = context.getString(R.string.randomFood)) }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )
                Text(
                    text = "美食轉盤",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = { navController.navigate(context.getString(R.string.str_member) + "/5") }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )
                Text(
                    text = "其他設定",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .size(60.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    userVM.username.value = ""
                    navController.navigate(context.getString(R.string.str_login))
                }
            ) {

            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "member",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "  ",
                    fontSize = 20.sp
                )
                Text(
                    text = "登出帳號",
                    fontSize = 20.sp
                )
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MemberMainScreen(rememberNavController(), viewModel())

}