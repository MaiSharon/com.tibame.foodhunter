package com.tibame.foodhunter.a871208s

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrivateViewModel : ViewModel() {
    var friend = mutableStateOf("")
    var friendimage= mutableStateOf<String?>(null)
    var friendnickname = mutableStateOf("")
    private val repository = PrivateChatRepository
    val PrivateChatFlow = repository.chatroomState
    val PrivateChatFlow2 = repository.chatroomState2
    suspend fun refreshchatroom(username:String) {
        repository.refreshchatroom(username)}
    private val _nowChatRoomId = MutableStateFlow("")
    val nowChatRoomId = _nowChatRoomId.asStateFlow()
    fun gotoChatRoom(roomId: String) {
        _nowChatRoomId.update {
            roomId
        }
    }
    var wall =mutableStateOf(false)
    private val _chatInput = MutableStateFlow("")
    val chatInput = _chatInput.asStateFlow()
    fun chatRoomInput(text: String) {
        _chatInput.update {
            text
        }
    }

    private val _chatRoom = MutableStateFlow(PrivateChat())
    val chatRoom: StateFlow<PrivateChat> = _chatRoom.asStateFlow()
    fun setDetailPrivateChat(privateChat: PrivateChat) {
        _chatRoom.update {
            privateChat
        }
    }


    suspend fun refreshmessage(username: String,friend: String) {
        _messageState.value = messageFetch(username,friend) ?: emptyList()

    }

    fun refreshmessage2(username: String,friend: String) {
        viewModelScope.launch {
            _messageState.value = messageFetch(username, friend) ?: emptyList()
        }

    }
    // 已追踪好友
    private val _messageState = MutableStateFlow(emptyList<Message>())
    val messageState = _messageState.asStateFlow()

    suspend fun messageFetch(message_id: String,
                             receiver_id: String): List<Message>? {
        return try {
            val url = "$serverUrl/member/messageFetch"
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("message_id", message_id)
            jsonObject.addProperty("receiver_id", receiver_id)
            // 发起网络请求
            val result = CommonPost(url, jsonObject.toString())
            Log.e("Response3", result) // 输出响应结果

            // 解析响应数据为 List<Friend>
            val messageListType = object : TypeToken<List<Message>>() {}.type
            val messageList: List<Message> = gson.fromJson(result, messageListType)

            messageList // 返回好友列表
        } catch (e: Exception) {
            Log.e("UserViewModel", "获取信息时出错: ${e.message}")
            null
        }
    }

    suspend fun sendMessage(
        message_id: String,
        receiver_id: String,
        message: String
    ): Boolean {
        try {
            // server URL
            val url = "$serverUrl/member/sendMessage"
            val gson = Gson()
            val jsonObject = JsonObject()

            // 將註冊資料轉成 JSON
            jsonObject.addProperty("username", message_id)
            jsonObject.addProperty("message_id", message_id)
            jsonObject.addProperty("receiver_id", receiver_id)
            jsonObject.addProperty("message", message)

            // 發出 POST 請求，取得註冊結果
            val result = CommonPost(url, jsonObject.toString())
            val responseJson = gson.fromJson(result, JsonObject::class.java)

            // 根據響應中的 logged 屬性來判斷是否註冊成功
            return responseJson.get("send").asBoolean
        } catch (e: Exception) {
            return false
        }
    }




}
