package com.tibame.foodhunter.a871208s

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.JsonObject
import com.tibame.foodhunter.R
import com.tibame.foodhunter.core.ui.theme.FColor
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun MemberInformationScreen(navController: NavHostController = rememberNavController(),userVM: UserViewModel) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 创建协程作用域来启动 suspend 函数 'imageSaved'
    val coroutineScope = rememberCoroutineScope()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
            uri?.let { imageUri ->
                // 壓縮圖片
                profileBitmap = getBitmapFromUri(context, imageUri)
                profileBitmap?.let {
                    // Convert Bitmap to Base64 string
                    val base64Image = encodeBitmapToBase64(it)

                    // Create JSON object to send to server
                    val json = JsonObject().apply {
                        addProperty("profileimage", base64Image)
                        // Other necessary data
                    }

                }
            }
        }
    )

    LaunchedEffect(selectedImageUri) {
        // 确保在协程中调用 suspend 函数
        selectedImageUri?.let { uri ->
            val bitmap = getBitmapFromUri(context, uri)
            bitmap?.let {
                val username = userVM.username.value
                // 在协程中调用 suspend 函数
                userVM.imageSaved(username, it)
            }
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    val usernameState = remember { mutableStateOf("") }
    val nicknameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val birthdayState = remember { mutableStateOf("") }
    val genderState = remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = userVM.getUserInfo(userVM.username.value)
            val user1 = userVM.image(userVM.username.value)// 替換為實際用戶 ID
            if (user != null) {
                usernameState.value = user.username // 獲取用戶名
                nicknameState.value = user.nickname
                emailState.value = user.email
                phoneState.value = user.phone
                birthdayState.value = user.birthday2
                genderState.value = user.gender
               user1?.profileImageBase64?.let { base64 ->
                   profileBitmap = userVM.decodeBase64ToBitmap(base64)
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },  // 點擊對話框以外區域，關閉對話框
            text = { Text(text = "確定要刪除帳號?") },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(context.getString(R.string.str_member) + "/4")

                    }  // 點擊確定按鈕，關閉對話框
                ) {
                    Text("確定")
                }
                Button(
                    onClick = { showDialog = false }  // 點擊確定按鈕，關閉對話框
                ) {
                    Text("取消")
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
                .fillMaxWidth()
                .background(FColor.Orange_3rd)
        ) {
            Text(
                text = "會員資料",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Box(
            // 設定內容物對齊方式為置中對齊
            contentAlignment = Alignment.Center,

            modifier = Modifier.fillMaxWidth().padding(32.dp,16.dp)
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
            Column(
                modifier = Modifier.fillMaxWidth().size(180.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    // 設定內容物對齊方式為置中對齊
                    modifier = Modifier
                        .size(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectedImageUri == null) {
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
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "image",
                            modifier = Modifier
                                .size(120.dp)
                                .border(BorderStroke(1.dp, Color(0xFFFF9800)), CircleShape)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // 如果有 Base64 图片数据，则显示解码后的图片，否则显示默认图片


                    TextButton(
                        modifier = Modifier
                            .size(120.dp),
                        shape = RoundedCornerShape(360.dp),
                        onClick = {
                            pickImageLauncher.launch(
                                PickVisualMediaRequest(
                                    // 設定只能挑選圖片
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {}
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Image",
                            modifier = Modifier.size(20.dp, 20.dp)
                        )
                    }
                }
            }

        }



        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(32.dp, 2.dp)
        ) {
            Text(
                text = "帳號",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = usernameState.value,
                fontSize = 20.sp,
                color = Color.Black
            )

        }
        Spacer(modifier = Modifier.size(3.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(32.dp, 2.dp),color =Color.LightGray)
        Spacer(modifier = Modifier.size(3.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(32.dp, 2.dp)
        ) {
            Text(
                text = "暱稱",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = nicknameState.value,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.size(3.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(32.dp, 2.dp),color =Color.LightGray)
        Spacer(modifier = Modifier.size(3.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(32.dp, 2.dp)
        ) {
            Text(
                text = "電子郵件",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = emailState.value,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.size(3.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(32.dp, 2.dp),color =Color.LightGray)
        Spacer(modifier = Modifier.size(3.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(32.dp, 2.dp)
        ) {
            Text(
                text = "手機",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = phoneState.value,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.size(3.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(32.dp, 2.dp),color =Color.LightGray)
        Spacer(modifier = Modifier.size(3.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(32.dp, 2.dp)
        ) {
            Text(
                text = "生日",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = birthdayState.value,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.size(3.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(32.dp, 2.dp),color =Color.LightGray)
        Spacer(modifier = Modifier.size(3.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(32.dp, 2.dp)
        ) {
            Text(
                text = "性別",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = genderState.value,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.size(3.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(32.dp, 2.dp),color =Color.LightGray)
        Spacer(modifier = Modifier.size(3.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .size(220.dp, 60.dp)
                    .padding(8.dp),
                onClick = { navController.navigate(context.getString(R.string.str_member) + "/3") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange_1st), // 背景顏色
                    contentColor = Color.White // 文字顏色
                )

            ) {
                Text(text = "修改資料")
            }
        }
/* 刪除帳號
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .size(140.dp, 60.dp)
                    .padding(8.dp),
                onClick = { showDialog = true }

            ) {
                Text(text = "刪除帳號")
            }
        }
*/
    }
    }



fun getBitmapFromUri(context: Context, uri: Uri, maxWidth: Int = 800, maxHeight: Int = 800): Bitmap? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            // 讀取圖片的尺寸
            BitmapFactory.decodeStream(inputStream, null, this)
            // 計算適當的縮放比例
            val scaleFactor = Math.max(outWidth / maxWidth, outHeight / maxHeight)
            inJustDecodeBounds = false
            inSampleSize = if (scaleFactor > 1) scaleFactor else 1
        }

        // 再次讀取圖片並進行壓縮
        inputStream?.close()
        val compressedBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)

        // 如果需要進一步縮小尺寸，可以這裡再進行縮放
        compressedBitmap?.let {
            val width = it.width
            val height = it.height
            if (width > maxWidth || height > maxHeight) {
                return@let Bitmap.createScaledBitmap(it, maxWidth, maxHeight, false)
            } else {

            }
        }
        compressedBitmap
    } catch (e: Exception) {
        Log.e("Image", "Failed to decode Uri to Bitmap", e)
        null
    }
}

fun encodeBitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

