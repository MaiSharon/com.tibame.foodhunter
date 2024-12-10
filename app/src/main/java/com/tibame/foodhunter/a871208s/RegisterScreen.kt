package com.tibame.foodhunter.a871208s

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.FormatStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController = rememberNavController(),
    userVM: UserViewModel
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var birthday by remember { mutableStateOf("") }
    val options = listOf("男", "女")
    var gender by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },  // 點擊對話框以外區域，關閉對話框
            text = { Text(text = "註冊失敗",
                color = colorResource(id = R.color.black)) },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }  // 點擊確定按鈕，關閉對話框
                ) {
                    Text("確定",
                        color = colorResource(id = R.color.orange_1st))
                }
            }
        )
    }
    var showDialog2 by remember { mutableStateOf(false) }
    if (showDialog2) {
        AlertDialog(
            onDismissRequest = { showDialog2 = false
                navController.navigate(context.getString(R.string.str_login))},  // 點擊對話框以外區域，關閉對話框
            text = { Text(text = "註冊成功",
                color = colorResource(id = R.color.black)) },
            confirmButton = {
                TextButton(
                    onClick = { showDialog2 = false
                        navController.navigate(context.getString(R.string.str_login))}  // 點擊確定按鈕，關閉對話框
                ) {
                    Text("確定",
                        color = colorResource(id = R.color.orange_1st))
                }
            }
        )
    }
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth().background(FColor.Orange_3rd)
        ) {
            Text(
                text = "建立新帳號",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }


        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "帳號",
                fontSize = 20.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text(text = "請輸入帳號", fontSize = 16.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().background(
                    color = if (username.isNotEmpty()) Color.White else FColor.Gary_20,
                    shape = RoundedCornerShape(12.dp)
                )
                    .border(
                        width = 1.dp,
                        color = if (username.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FColor.Orange_1st,
                    unfocusedBorderColor = FColor.Gary,// 設定聚焦時的邊框顏色
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "密碼",
                fontSize = 20.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(text = "請輸入密碼", fontSize = 16.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().background(
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
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "暱稱",
                fontSize = 20.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = { Text(text = "請輸入暱稱", fontSize = 16.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().background(
                    color = if (nickname.isNotEmpty()) Color.White else FColor.Gary_20,
                    shape = RoundedCornerShape(12.dp)
                )
                    .border(
                        width = 1.dp,
                        color = if (nickname.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FColor.Orange_1st,
                    unfocusedBorderColor = FColor.Gary,// 設定聚焦時的邊框顏色
                )
            )

        }
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
                modifier = Modifier.fillMaxWidth().background(
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
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "手機號碼",
                fontSize = 20.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text(text = "請輸入號碼", fontSize = 16.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().background(
                    color = if (phone.isNotEmpty()) Color.White else FColor.Gary_20,
                    shape = RoundedCornerShape(12.dp)
                )
                    .border(
                        width = 1.dp,
                        color = if (phone.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FColor.Orange_1st,
                    unfocusedBorderColor = FColor.Gary,// 設定聚焦時的邊框顏色
                )
            )

        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "生日",
                fontSize = 20.sp,
                color = Color.Black
            )
            OutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it },
                placeholder = { Text(text = "請輸入生日", fontSize = 16.sp) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "date",
                        modifier = Modifier.clickable {
                            showDatePickerDialog = true
                        }

                    )
                    if (showDatePickerDialog) {
                        MyDatePickerDialog(
                            onDismissRequest = {
                                birthday = ""
                                showDatePickerDialog = false
                            },
                            onConfirm = { utcTimeMillis ->
                                birthday = "${
                                    utcTimeMillis?.let {
                                        Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC"))
                                            .toLocalDate()
                                            .format(ofLocalizedDate(FormatStyle.MEDIUM))
                                    } ?: ""
                                }"
                                showDatePickerDialog = false
                            },
                            onDismiss = {
                                birthday = ""
                                showDatePickerDialog = false
                            }
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().background(
                    color = if (birthday.isNotEmpty()) Color.White else FColor.Gary_20,
                    shape = RoundedCornerShape(12.dp)
                )
                    .border(
                        width = 1.dp,
                        color = if (birthday.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FColor.Orange_1st,
                    unfocusedBorderColor = FColor.Gary,// 設定聚焦時的邊框顏色
                )
            )

        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "性別",
                fontSize = 20.sp,
                color = Color.Black
            )
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    readOnly = true,
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.fillMaxWidth().background(
                        color = if (gender.isNotEmpty()) Color.White else FColor.Gary_20,
                        shape = RoundedCornerShape(0.dp)
                    )
                        .border(
                            width = 1.dp,
                            color = if (gender.isNotEmpty()) FColor.Orange_1st else FColor.Gary,
                            shape = RoundedCornerShape(0.dp)
                        ).menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FColor.Orange_1st,
                        unfocusedBorderColor = FColor.Gary,// 設定聚焦時的邊框顏色
                    ),

                    value = gender,
                    onValueChange = {
                        gender = it
                        expanded = true
                    },
                    singleLine = true,
                    label = { Text("選擇性別",
                        color = Color.Black
                    )
                            },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth().background(
                        color = Color.White ,
                        shape = RoundedCornerShape(0.dp)
                    )
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(0.dp)
                        )
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .size(120.dp, 60.dp)
                    .padding(8.dp),
                onClick = {
                    coroutineScope.launch {
                        val register = userVM.register(
                            username,
                            password,
                            nickname,
                            email,
                            phone,
                            gender,
                            convertDateFormat(birthday)
                        )
                        if (register) {
                            showDialog2 = true

                        } else {
                            showDialog = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange_1st), // 背景顏色
                    contentColor = Color.White // 文字顏色
                )
            ) {
                Text(text = "建立")
            }
            Button(
                modifier = Modifier
                    .size(120.dp, 60.dp)
                    .padding(8.dp),
                onClick = {
                            navController.navigate(context.getString(R.string.str_login))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange_3rd), // 背景顏色
                    contentColor = Color.White // 文字顏色
                )
            ) {
                Text(text = "返回")
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableYear(year: Int): Boolean {
                return year <= 2024
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(datePickerState.selectedDateMillis)
                }
            ) {
                Text("確定",
                    color = colorResource(id = R.color.orange_1st))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消",
                    color = colorResource(id = R.color.black))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun convertDateFormat(input: String): String {
    // 定義輸入和輸出的日期格式
    val inputFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return try {
        // 解析輸入字串
        val date = inputFormat.parse(input)
        // 將日期轉換為所需的格式並返回
        outputFormat.format(date)
    } catch (e: Exception) {
        // 處理可能的解析異常
        input
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {

    MaterialTheme {
        Main()
    }

}