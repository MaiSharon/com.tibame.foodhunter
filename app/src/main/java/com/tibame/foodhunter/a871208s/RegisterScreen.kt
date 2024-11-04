package com.tibame.foodhunter.a871208s

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.Main
import com.tibame.foodhunter.R
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
    var username by remember { mutableStateOf("123456") }
    var password by remember { mutableStateOf("123456") }
    var nickname by remember { mutableStateOf("測試2") }
    var email by remember { mutableStateOf("abc") }
    var phone by remember { mutableStateOf("0911122233") }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var birthday by remember { mutableStateOf("2024年11月5日") }
    val options = listOf("男", "女")
    var gender by remember { mutableStateOf("男") }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },  // 點擊對話框以外區域，關閉對話框
            text = { Text(text = "註冊失敗") },
            confirmButton = {
                Button(
                    onClick = { showDialog = false }  // 點擊確定按鈕，關閉對話框
                ) {
                    Text("確定")
                }
            }
        )
    }
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
    ) {

        Text(
            text = "建立新帳號",
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )
        HorizontalDivider(
            modifier = Modifier.size(500.dp, 1.dp),
            color = Color.Blue
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "帳號",
                fontSize = 20.sp,
                color = Color.Blue
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text(text = "請輸入帳號", fontSize = 18.sp) },
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
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
                color = Color.Blue
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text(text = "請輸入密碼", fontSize = 18.sp) },
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
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
                color = Color.Blue
            )
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = { Text(text = "請輸入暱稱", fontSize = 18.sp) },
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
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
                color = Color.Blue
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = "請輸入信箱", fontSize = 18.sp) },
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
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
                color = Color.Blue
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text(text = "請輸入號碼", fontSize = 18.sp) },
                singleLine = true,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
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
                color = Color.Blue
            )
            OutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it },
                placeholder = { Text(text = "請輸入生日", fontSize = 18.sp) },
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
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth(),
            )

        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp, 2.dp)
        ) {
            Text(
                text = "性別",
                fontSize = 20.sp,
                color = Color.Blue
            )
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    //shape = RoundedCornerShape(32.dp),
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = gender,
                    onValueChange = {
                        gender = it
                        expanded = true
                    },
                    singleLine = true,
                    label = { Text("選擇性別") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
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
                            navController.navigate(context.getString(R.string.str_login))
                        } else {
                            showDialog = true
                        }
                    }
                }
            ) {
                Text(text = "建立")
            }
            Button(
                modifier = Modifier
                    .size(120.dp, 60.dp)
                    .padding(8.dp),
                onClick = {
                    coroutineScope.launch {
                            navController.navigate(context.getString(R.string.str_login))
                    }
                }
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
            Button(
                onClick = {
                    onConfirm(datePickerState.selectedDateMillis)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
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