package com.tibame.foodhunter.a871208s

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UserViewModel : ViewModel() {
    var username = mutableStateOf("")
    var emailFindPassword = mutableStateOf("")
    // 登入
// 添加 memberId 的 StateFlow
    private val _memberId = MutableStateFlow(0)
    val memberId: StateFlow<Int> = _memberId.asStateFlow()
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()


    var profileBitmap by mutableStateOf<Bitmap?>(null)



    // 在登入成功時設置 memberId
    suspend fun login(username: String, password: String): Boolean {
        try {
            val url = "$serverUrl/member/login"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)
            jsonObject.addProperty("password", password)

            val result = CommonPost(url, jsonObject.toString())
            val responseJson = gson.fromJson(result, JsonObject::class.java)

            // 如果登入成功，立即獲取並設置 memberId
            if (responseJson.get("logged").asBoolean) {
                // 獲取用戶信息並設置 memberId
                val user = getUserInfo(username)
                val user1 = image(username)
                if (user != null) {
                    _memberId.value = user.id
                    _nickname.value = user.nickname
                    user1?.profileImageBase64?.let { base64 ->
                        profileBitmap = decodeBase64ToBitmap(base64)
                    }
                }
                return true
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun register(
        username: String,
        password: String,
        nickname: String,
        email: String,
        phone: String,
        gender: String,
        birthday: String,
    ): Boolean {
        try {
            // server URL
            val url = "$serverUrl/member/register"
            val gson = Gson()
            val jsonObject = JsonObject()

            // 將註冊資料轉成 JSON
            jsonObject.addProperty("username", username)
            jsonObject.addProperty("password", password)
            jsonObject.addProperty("nickname", nickname)
            jsonObject.addProperty("email", email)
            jsonObject.addProperty("phone", phone)
            jsonObject.addProperty("gender", gender)
            jsonObject.addProperty("birthday", birthday)

            // 發出 POST 請求，取得註冊結果
            val result = CommonPost(url, jsonObject.toString())
            val responseJson = gson.fromJson(result, JsonObject::class.java)

            // 根據響應中的 logged 屬性來判斷是否註冊成功
            return responseJson.get("registered").asBoolean
        } catch (e: Exception) {
            return false
        }
    }


    suspend fun getUserInfo(username: String): User? {
        return try {
            val url = "$serverUrl/member/getInfo"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)

            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response", result) // 输出响应结果

            // 使用 TypeToken 获取 User 的类型
            val userType = object : TypeToken<User>() {}.type
            val user = gson.fromJson<User>(result, userType) // 显式指定类型

            user
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取用户信息时出错: ${e.message}")
            null
        }
    }

    // 用戶數據類
    data class User(
        val id: Int,  // 确保添加 id 字段
        val username: String,
        val password: String,
        val nickname: String,
        val email: String,
        val phone: String,
        val registrationdate: String,
        val profileimage: InputStream?,
        val gender: String,
        val birthday: String,
        val birthday2: String,
        val profileImageBase64: String? = null
    )






    suspend fun save(
        username: String,
        password: String,
        nickname: String,
        email: String,
        phone: String,
    ): Boolean {
        try {
            // server URL
            val url = "$serverUrl/member/save"
            val gson = Gson()
            val jsonObject = JsonObject()

            // 將註冊資料轉成 JSON
            jsonObject.addProperty("username", username)
            jsonObject.addProperty("password", password)
            jsonObject.addProperty("nickname", nickname)
            jsonObject.addProperty("email", email)
            jsonObject.addProperty("phone", phone)

            // 發出 POST 請求，取得註冊結果
            val result = CommonPost(url, jsonObject.toString())
            val responseJson = gson.fromJson(result, JsonObject::class.java)

            // 根據響應中的 logged 屬性來判斷是否註冊成功
            return responseJson.get("save").asBoolean
        } catch (e: Exception) {
            return false
        }
    }


    suspend fun imageSaved(username: String, profileImage: Bitmap?): Boolean {
        try {
            // 检查图片是否为空
            if (profileImage == null) {
                Log.e("UserViewModel", "没有提供图片")
                return false
            }

            // 将 Bitmap 转换为 Base64 字符串
            val base64Image = encodeBitmapToBase64(profileImage)

            if (base64Image.isEmpty()) {
                Log.e("UserViewModel", "Base64 图片为空")
                return false
            }

            // 打印 Base64 字符串以调试
            Log.e("UserViewModel", "Base64 image: $base64Image")

            // 准备服务器 URL
            val url = "$serverUrl/member/imageSave"

            // 创建包含用户名和 Base64 图片字符串的 JSON 对象
            val jsonObject = JsonObject().apply {
                addProperty("username", username)
                addProperty("profileimage", base64Image)
            }

            // 发送 POST 请求
            val result = CommonPost(url, jsonObject.toString())

            // 打印原始响应用于调试
            Log.e("UserViewModel", "Response: $result")

            // 如果响应为空或无效，记录日志并返回 false
            if (result.isEmpty()) {
                Log.e("Response2", "服务器返回空响应")
                return false
            }

            // 解析 JSON 响应
            val gson = Gson()
            val responseJson = gson.fromJson(result, JsonObject::class.java)

            // 如果解析的 JSON 是 null，说明响应格式无效
            if (responseJson == null) {
                Log.e("Response3", "无效的 JSON 响应")
                return false
            }

            // 打印解析后的响应，查看其中的字段
            Log.e("Parsed Response", responseJson.toString())

            // 检查响应中的 "imageSave" 是否为 true
            return responseJson.get("imageSave")?.asBoolean ?: false
        } catch (e: Exception) {
            // 捕获异常并打印错误信息
            Log.e("UserViewModel", "上传图片失败: ${e.message}")
            return false
        }
    }

    fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            10,
            byteArrayOutputStream
        )  // 80 表示壓縮品質，您可以根據需要調整
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }


    suspend fun image(username: String): User? {
        return try {
            val url = "$serverUrl/member/image"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)

            // 发送 POST 请求
            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response", result) // 输出响应结果

            // 使用 TypeToken 获取 User 的类型
            val userType = object : TypeToken<User>() {}.type
            val user = gson.fromJson<User>(result, userType)
            // 显式指定类型

            // 处理图片：如果 profileImageBase64 不为空

            user?.profileImageBase64?.let {
                Log.d("ProfileImage", "Profile image base64: $it")
            }

            user
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取用户信息时出错: ${e.message}")
            null
        }
    }

    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            // 打印原始 Base64 字符串
            Log.d("DecodeBase64", "Original Base64 String: $base64String")

            // 去除 Base64 字符串的前缀（例如 data:image/png;base64,）
            val cleanBase64String = if (base64String.startsWith("data:image/")) {
                // 如果有前缀，去除前缀部分
                val cleanedString = base64String.replaceFirst(
                    "^[A-Za-z0-9+/=]+(?:[ \t\r\n]*;[ \t\r\n]*charset=[A-Za-z0-9-]+)?(?:[ \t\r\n]*base64)?,?".toRegex(),
                    ""
                )
                // 打印去除前缀后的 Base64 字符串
                Log.d("DecodeBase64", "Cleaned Base64 String: $cleanedString")
                cleanedString
            } else {
                // 如果没有前缀，直接使用原始字符串
                base64String
            }

            // 解码 Base64 字符串
            val decodedString = Base64.decode(cleanBase64String, Base64.DEFAULT)

            // 打印解码后的字节数组大小（查看是否解码成功）
            Log.d("DecodeBase64", "Decoded Byte Array Size: ${decodedString.size}")

            // 将字节数组转换为 Bitmap
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            // 如果解码失败，记录 Bitmap 为 null
            if (bitmap == null) {
                Log.e("DecodeBase64", "Failed to decode Base64 to Bitmap")
            }

            bitmap
        } catch (e: Exception) {
            Log.e("DecodeBase64", "Base64 解码失败: ${e.message}")
            null
        }

    }

    suspend fun getEmailInfo(email: String): User? {
        return try {
            val url = "$serverUrl/member/getEmailInfo"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("email", email)

            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response", result) // 输出响应结果

            // 使用 TypeToken 获取 User 的类型
            val userType = object : TypeToken<User>() {}.type
            val user = gson.fromJson<User>(result, userType) // 显式指定类型

            user
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取用户信息时出错: ${e.message}")
            null
        }


    }
    suspend fun saveNewPassword(
        email: String,
        password: String,
    ): Boolean {
        try {
            // server URL
            val url = "$serverUrl/member/saveNewPassword"
            val gson = Gson()
            val jsonObject = JsonObject()

            // 將註冊資料轉成 JSON
            jsonObject.addProperty("email", email)
            jsonObject.addProperty("password", password)
            // 發出 POST 請求，取得註冊結果
            val result = CommonPost(url, jsonObject.toString())
            val responseJson = gson.fromJson(result, JsonObject::class.java)

            // 根據響應中的 logged 屬性來判斷是否註冊成功
            return responseJson.get("save").asBoolean
        } catch (e: Exception) {
            return false
        }
    }
    suspend fun getMemberUsername(memberId: Int): String? {
        return try {
            Log.d("UserViewModel", "=== 開始獲取 username ===")

            // 檢查 URL 和請求內容
            val url = "$serverUrl/post/GetUsername"  // 注意這裡改成跟其他 API 一樣的路徑格式
            val jsonObject = JsonObject().apply {
                addProperty("memberId", memberId)
            }

            Log.d("UserViewModel", "請求 URL: $url")
            Log.d("UserViewModel", "請求內容: ${jsonObject}")

            // 使用 CommonPost 發送請求
            val result = CommonPost(url, jsonObject.toString())
            Log.d("UserViewModel", "API 回應: $result")

            if (result.isNotEmpty()) {
                val gson = Gson()
                val response = gson.fromJson(result, JsonObject::class.java)
                val username = response.get("username")?.asString
                Log.d("UserViewModel", "解析得到的 username: $username")
                username
            } else {
                Log.e("UserViewModel", "API 回應為空")
                null
            }
        } catch (e: Exception) {
            Log.e("UserViewModel", "獲取 username 失敗", e)
            null
        }
    }

}



