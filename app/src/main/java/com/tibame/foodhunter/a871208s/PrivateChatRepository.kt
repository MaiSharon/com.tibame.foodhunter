package com.tibame.foodhunter.a871208s

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PrivateChatRepository {



    suspend fun refreshchatroom(username: String) {
        _chatroomState.value = chatroomFetch(username) ?: emptyList()
        _chatroomState2.value = chatroomFetch2(username) ?: emptyList()
    }
    // 已追踪好友
    private val _chatroomState = MutableStateFlow<List<PrivateChat>>(emptyList())
    val chatroomState: StateFlow<List<PrivateChat>> = _chatroomState

    // 建议追踪好友
    private val _chatroomState2 = MutableStateFlow<List<PrivateChat>>(emptyList())
    val chatroomState2: StateFlow<List<PrivateChat>> = _chatroomState2



    suspend fun chatroomFetch(username: String): List<PrivateChat>? {
        return try {
            val url = "$serverUrl/member/chatroomFetch"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)

            // 发起网络请求
            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response2", result) // 输出响应结果

            // 解析响应数据为 List<Friend>
            val chatroomListType = object : TypeToken<List<PrivateChat>>() {}.type
            val chatroomList: List<PrivateChat> = gson.fromJson(result, chatroomListType)

            chatroomList // 返回好友列表
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取好友信息时出错: ${e.message}")
            null
        }
    }

    suspend fun chatroomFetch2(username: String): List<PrivateChat>? {
        return try {
            val url = "$serverUrl/member/chatroomFetch2"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("username", username)

            // 发起网络请求
            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response2", result) // 输出响应结果

            // 解析响应数据为 List<Friend>
            val chatroomListType = object : TypeToken<List<PrivateChat>>() {}.type
            val chatroomList: List<PrivateChat> = gson.fromJson(result, chatroomListType)

            chatroomList // 返回好友列表
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取好友信息时出错: ${e.message}")
            null
        }
    }
}