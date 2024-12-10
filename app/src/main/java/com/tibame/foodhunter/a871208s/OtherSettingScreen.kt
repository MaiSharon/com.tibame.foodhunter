package com.tibame.foodhunter.a871208s

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor

@Composable
fun OtherSettingScreen(
    navController: NavHostController = rememberNavController(),
    pChatVM: PrivateViewModel
) {
    val context = LocalContext.current
    var sw1 = pChatVM.wall.value

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(FColor.Orange_3rd)
        ) {
            Text(
                text = "其他設定",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(FColor.Orange_4th)
        ) {
            Text(
                text = "私聊",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Row {


            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(16.dp, 2.dp)
            ) {

                Text(
                    text = "阻擋訊息",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Text(
                    text = "開啟功能後可以阻擋非好友的訊息",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.size(2.dp))

            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 2.dp)
            ) {
                SwitchWithText(checked = sw1, onCheckedChange = {
                    sw1 = it
                    pChatVM.wall.value = it  // 更新 ViewModel 中的 wall 状态
                })
            }
        }



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .size(120.dp, 60.dp)
                    .padding(8.dp),
                onClick = { navController.navigate(context.getString(R.string.str_member)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange_1st), // 背景顏色
                    contentColor = Color.White // 文字顏色
                )

            ) {
                Text(text = "確定")
            }
        }

    }
}

@Composable
fun SwitchWithText(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 建立switch
        Switch(
            // 開關狀態
            checked = checked,
            // 開關狀態改變時呼叫
            onCheckedChange = onCheckedChange,
            // 設定圖示：選取時顯示勾選圖示，取消選取時則不顯示圖示
            thumbContent =
            if (checked) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Check",
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            },
            // 設定顏色
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colorResource(id = R.color.orange_1st),
                uncheckedThumbColor = Color.DarkGray,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}




