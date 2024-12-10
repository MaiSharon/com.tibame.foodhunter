package com.tibame.foodhunter.a871208s

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tibame.foodhunter.Main
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.titleFamily
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    navController: NavHostController = rememberNavController(),
    userVM: UserViewModel,
    func:()->Unit
) {

    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },  // 點擊對話框以外區域，關閉對話框
            text = {
                Text(
                    text = "帳號或密碼錯誤",
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
            },
            containerColor = Color.White
        )
    }
    Box(
        // 設定內容物對齊方式為置中對齊
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Default Image",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.size(120.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = titleFamily,
                color = colorResource(R.color.orange_1st)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "User ID") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "password") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.clickable {
                            password = ""
                        }
                    )
                },
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End

            ) {
                TextButton(

                    modifier = Modifier
                        .size(90.dp, 35.dp),

                    onClick = { navController.navigate(context.getString(R.string.str_login) + "/3") }

                ) {

                    Text(text = "忘記密碼?")
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
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.orange_2nd)),
                    onClick = {

                        coroutineScope.launch {
                            val logged = userVM.login(username, password)
                            if (logged) {
                                userVM.username.value = username
                                navController.navigate(context.getString(R.string.str_Recommended_posts))
                                func()
                            } else {
                                showDialog = true
                            }
                        }
                    }
                ) {
                    Text(text = "登入")
                }
                Button(
                    modifier = Modifier
                        .size(120.dp, 60.dp)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.orange_2nd)),
                    onClick = { navController.navigate(context.getString(R.string.str_login) + "/2") }

                ) {
                    Text(text = "註冊")
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {

    MaterialTheme {
        Main()
    }

}