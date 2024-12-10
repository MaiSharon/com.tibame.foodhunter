package com.tibame.foodhunter.a871208s

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FriendViewModel: ViewModel() {

    // 已追踪好友
    private val _friendState = MutableStateFlow<List<Friend>>(emptyList())
    val friendState: StateFlow<List<Friend>> = _friendState

    // 建议追踪好友
    private val _friendState2 = MutableStateFlow<List<Friend>>(emptyList())
    val friendState2: StateFlow<List<Friend>> = _friendState2

    suspend fun refreshFriends(username: String) {
        _friendState.value = friendFetch(username) ?: emptyList()
    }
    suspend fun refreshFriends2(username: String) {
        _friendState2.value = friendFetch2(username) ?: emptyList()
    }
    suspend fun friendAdd(
        username: String,
        friend: String,

    ): Boolean {
        try {
            // server URL
            val url = "$serverUrl/member/friendAdd"
            val gson = Gson()
            val jsonObject = JsonObject()

            // 將註冊資料轉成 JSON
            jsonObject.addProperty("username", username)
            jsonObject.addProperty("friend", friend)


            // 發出 POST 請求，取得註冊結果
            val result = CommonPost(url, jsonObject.toString())
            val responseJson = gson.fromJson(result, JsonObject::class.java)
            Log.e("Response", result) // 输出响应结果
            // 根據響應中的 logged 屬性來判斷是否註冊成功
            return  responseJson.get("Add").asBoolean
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun friendDel(
        username: String,
        friend: String,

        ): Boolean {
        try {
            // server URL
            val url = "$serverUrl/member/friendDel"
            val gson = Gson()
            val jsonObject = JsonObject()

            // 將註冊資料轉成 JSON
            jsonObject.addProperty("username", username)
            jsonObject.addProperty("friend", friend)


            // 發出 POST 請求，取得註冊結果
            val result = CommonPost(url, jsonObject.toString())
            val responseJson = gson.fromJson(result, JsonObject::class.java)
            Log.e("Response", result) // 输出响应结果
            // 根據響應中的 logged 屬性來判斷是否註冊成功
            return  responseJson.get("Del").asBoolean
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun friendFetch(username: String): List<Friend>? {
        return try {
            val url = "$serverUrl/member/friendFetch"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)

            // 发起网络请求
            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response3", result) // 输出响应结果

            // 解析响应数据为 List<Friend>
            val friendListType = object : TypeToken<List<Friend>>() {}.type
            val friendList: List<Friend> = gson.fromJson(result, friendListType)

            friendList // 返回好友列表
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取好友信息时出错: ${e.message}")
            null
        }
    }


    suspend fun friendFetch2(username: String): List<Friend>? {
        return try {
            val url = "$serverUrl/member/friendFetch2"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)

            // 发起网络请求
            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response3", result) // 输出响应结果

            // 解析响应数据为 List<Friend>
            val friendListType = object : TypeToken<List<Friend>>() {}.type
            val friendList: List<Friend> = gson.fromJson(result, friendListType)

            friendList // 返回好友列表
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取好友信息时出错: ${e.message}")
            null
        }
    }
}