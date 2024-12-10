package com.tibame.foodhunter.a871208s

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun PrivateChatRoom(
    PrivateRoomId: String,
    pChatVM: PrivateViewModel,
    userVM: UserViewModel
) {
    pChatVM.gotoChatRoom(PrivateRoomId)
    val item = pChatVM.messageState.collectAsState()
    val scope = rememberCoroutineScope()
    val memberIdState by userVM.memberId.collectAsState()
    LaunchedEffect(Unit) {
        scope.launch {
            // 確保在畫面初次顯示時抓取好友資料
            pChatVM.refreshmessage(userVM.username.value, pChatVM.friend.value)
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.orange_6th)),
        reverseLayout = true
    ) {
        items(item.value) { message ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.orange_6th))
                    .padding(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (message.message_id.toInt() == memberIdState) {
                    true -> {}  // 如果是当前用户发送的消息
                    else ->
                        Box(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            val imageBitmap =
                                pChatVM.friendimage.value?.let { base64ToBitmap(it) }
                            imageBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "friend",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .border(
                                            BorderStroke(1.dp, Color(0xFFFF9800)),
                                            CircleShape
                                        )
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } ?: run {
                                // 顯示預設圖像或占位圖像
                                Image(
                                    painter = painterResource(id = R.drawable.account_circle),
                                    contentDescription = "default friend image",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .border(
                                            BorderStroke(1.dp, Color(0xFFFF9800)),
                                            CircleShape
                                        )
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, bottom = 8.dp),
                    horizontalAlignment = when (message.message_id.toInt() == memberIdState) {
                        true -> Alignment.End
                        else -> Alignment.Start
                    }
                ) {
                    when (message.message_id.toInt() == memberIdState) {
                        true -> {}
                        else -> Text(pChatVM.friendnickname.value)
                    }
                    Row(
                        verticalAlignment = Alignment.Bottom
                    )
                    {
                        if (message.message_id.toInt() == memberIdState) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                                text = convertToTime(message.message_time),
                                fontSize = 12.sp
                            )
                        }
                        Column(
                            modifier = Modifier
                                .background(
                                    when (message.message_id.toInt() == memberIdState) {
                                        true -> colorResource(R.color.orange_4th)
                                        else -> Color.White
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = message.message
                            )
                        }
                        if (message.message_id.toInt() != memberIdState) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                ),
                                text = convertToTime(message.message_time),
                                fontSize = 12.sp
                            )
                        }
                    }

                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatRoomTopBar(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    pChatVM: PrivateViewModel
) {
    val chatRoom = pChatVM.chatRoom.collectAsState().value
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = chatRoom.nickname,
                color = Color.White//colorResource(R.color.orange_1st)
            )
        },
        navigationIcon = {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.str_back),
                modifier = Modifier.clickable {
                    pChatVM.chatRoomInput("")
                    navController.popBackStack()
                },
                tint = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.orange_2nd)
        )
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatRoomBottomBar(
    pChatVM: PrivateViewModel,
    userVM: UserViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var chatInput by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            TextField(
                value = chatInput,
                onValueChange = {
                    chatInput = it
                },
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = colorResource(R.color.orange_4th),
                    unfocusedContainerColor = colorResource(R.color.orange_4th)
                ),
                placeholder = {
                    Text(text = "請輸入訊息")
                }
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "",
                tint = FColor.Orange_1st,//MaterialTheme.colorScheme.primary
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        val message = pChatVM.sendMessage(
                            userVM.username.value,
                            pChatVM.friend.value,
                            chatInput
                        )
                        if (message) {
                            chatInput = ""
                            // 確保在畫面初次顯示時抓取好友資料
                            pChatVM.refreshmessage(userVM.username.value, pChatVM.friend.value)
                        }
                    }
                }

            )
        }


    }
}

fun convertToTime(inputDate: String): String {
    // 定义输入日期格式
    Log.e("TAG","input $inputDate")
    val inputFormat = SimpleDateFormat("MMM dd, yyyy, hh:mm:ss\u202Fa", Locale.ENGLISH)

    // 定义输出日期格式
    val outputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

    // 解析输入的日期字符串
    val date = inputFormat.parse(inputDate)

    // 转换为需要的时间格式并返回
    return outputFormat.format(date)
}