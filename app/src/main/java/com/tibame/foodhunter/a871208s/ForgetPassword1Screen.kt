package com.tibame.foodhunter.a871208s

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.Main
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor
import kotlinx.coroutines.launch

@Composable
fun ForgetPassword1Screen(
    navController: NavHostController = rememberNavController(),
    callback: @Composable () -> Unit, userVM: UserViewModel
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },  // 點擊對話框以外區域，關閉對話框
            text = {
                Text(
                    text = "驗證碼錯誤",
                    color = colorResource(id = R.color.black)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }  // 點擊確定按鈕，關閉對話框
                ) {
                    Text(
                        "確定",
                        color = colorResource(id = R.color.orange_1st)
                    )
                }
            }
        )
    }
    var showDialog2 by remember { mutableStateOf(false) }
    if (showDialog2) {
        AlertDialog(
            onDismissRequest = { showDialog2 = false },  // 點擊對話框以外區域，關閉對話框
            text = {
                Text(
                    text = "電子信箱錯誤",
                    color = colorResource(id = R.color.black)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog2 = false }  // 點擊確定按鈕，關閉對話框
                ) {
                    Text(
                        "確定",
                        color = colorResource(id = R.color.orange_1st)
                    )
                }
            }
        )
    }
    var showDialog3 by remember { mutableStateOf(false) }
    if (showDialog3) {
        AlertDialog(
            onDismissRequest = { showDialog3 = false },  // 點擊對話框以外區域，關閉對話框
            text = {
                Text(
                    text = "驗證碼已發送",
                    color = colorResource(id = R.color.black)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog3 = false }  // 點擊確定按鈕，關閉對話框
                ) {
                    Text(
                        "確定",
                        color = colorResource(id = R.color.orange_1st)
                    )
                }
            }
        )
    }
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(FColor.Orange_3rd)
        ) {
            Text(
                text = "忘記密碼",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "電子信箱",
                fontSize = 20.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = "請輸入信箱", fontSize = 16.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (email.isNotEmpty()) Color.White else FColor.Gary_20,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (email.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FColor.Orange_1st,
                    unfocusedBorderColor = FColor.Gary,// 設定聚焦時的邊框顏色
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .size(200.dp, 60.dp)
                    .padding(8.dp),
                onClick = {
                    coroutineScope.launch {
                        if (userVM.getEmailInfo(email) == null) {
                            showDialog2 = true
                        } else {
                            showDialog3 = true
                            userVM.emailFindPassword.value = email
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange_1st), // 背景顏色
                    contentColor = Color.White // 文字顏色
                )
            ) {
                Text(text = "發送驗證碼")
            }
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "驗證碼",
                fontSize = 20.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(text = "請輸入驗證碼", fontSize = 16.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (password.isNotEmpty()) Color.White else FColor.Gary_20,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (password.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FColor.Orange_1st,
                    unfocusedBorderColor = FColor.Gary,// 設定聚焦時的邊框顏色
                )
            )
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
                onClick = {
                    if (password == "000000" && userVM.emailFindPassword.value != "") {
                        navController.navigate(context.getString(R.string.str_login) + "/4")
                    } else {
                        showDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange_1st), // 背景顏色
                    contentColor = Color.White // 文字顏色
                )
            ) {
                Text(text = "下一步")
            }
            Button(
                modifier = Modifier
                    .size(120.dp, 60.dp)
                    .padding(8.dp),
                onClick = {
                    userVM.emailFindPassword.value = ""
                    navController.navigate(context.getString(R.string.str_login))
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange_3rd), // 背景顏色
                    contentColor = Color.White // 文字顏色
                )
            ) {
                Text(text = "返回")
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun ForgetPassword1ScreenPreview() {

    MaterialTheme {
        Main()
    }

}