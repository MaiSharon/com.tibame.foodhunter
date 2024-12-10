package com.tibame.foodhunter.ai_ying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.tibame.foodhunter.a871208s.UserViewModel
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class GroupViewModel : ViewModel() {
    var userVM: UserViewModel? = null
    fun getUserName(): String {
        return userVM?.username?.value ?: ""
    }

    private val repository = GroupRepository
    val groupChat = repository.groupChatList
    fun getGroupChatList(username: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            val gson = Gson()
            var jsonObject = JsonObject()
            jsonObject.addProperty("username", username)
            val result = CommonPost("$serverUrl/group/list", jsonObject.toString())
            jsonObject = gson.fromJson(result, JsonObject::class.java)
            val collectionType = object : TypeToken<List<GroupChat>>() {}.type
            val list =
                gson.fromJson<List<GroupChat>>(jsonObject.get("result").asString, collectionType)
                    ?: emptyList()
            val incList = emptyList<GroupChat>().toMutableList()
            val endList = emptyList<GroupChat>().toMutableList()
            list.forEach {
                val date = LocalDate.parse(it.time, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val today = LocalDate.now()
                if (today > date) {
                    it.state = 0
                    endList.add(it)
                } else {
                    incList.add(it)
                }
            }
            incList.add(0, GroupChat(name = "進行中", state = 99))
            endList.add(0, GroupChat(name = "已結束", state = 98))
            repository.updateGroupChatList(incList.plus(endList))
            _isLoading.update { false }
        }
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _isAvatarLoading = MutableStateFlow(true)
    val isAvatarLoading = _isAvatarLoading.asStateFlow()

    private val _nowChatRoomId = MutableStateFlow(999)
    val nowChatRoomId = _nowChatRoomId.asStateFlow()
    fun gotoChatRoom(roomId: Int) {
        _nowChatRoomId.update {
            roomId
        }
    }


    private val _chatInput = MutableStateFlow("")
    val chatInput = _chatInput.asStateFlow()
    fun chatRoomInput(text: String) {
        _chatInput.update {
            text
        }
    }

    private val _chatRoom = MutableStateFlow(GroupChat())
    val chatRoom: StateFlow<GroupChat> = _chatRoom.asStateFlow()
    fun setDetailGroupChat(groupChat: GroupChat) {
        _chatRoom.update {
            groupChat
        }
    }

    fun getGroupChatDetailFromId(id: Int) {
        _chatRoom.update {
            groupChat.value.find {
                it.id == id && it.state != 99
            } ?: GroupChat()
        }
    }

    private val _groupCreateData = MutableStateFlow(GroupCreateData())
    val groupCreateData: StateFlow<GroupCreateData> = _groupCreateData.asStateFlow()
    fun setGroupCreateData(data: GroupCreateData) {
        _groupCreateData.update {
            data
        }
    }

    private val _groupSearchData = MutableStateFlow(GroupSearchData())
    val groupSearchData: StateFlow<GroupSearchData> = _groupSearchData.asStateFlow()
    fun setGroupSearchData(data: GroupSearchData) {
        _groupSearchData.update {
            data
        }
    }

    private val _groupSearchResult = MutableStateFlow(emptyList<GroupSearchResult>())
    var groupSearchResult = _groupSearchResult.asStateFlow()
    fun getGroupSearchResult() {
        groupSearchResult = repository.groupSearchResult
    }

    var showEditGroup = MutableStateFlow(false)
    var showEditMember = MutableStateFlow(false)
    fun setShowEditGroup(show: Boolean) {
        showEditGroup.update { show }
    }

    fun setShowEditMember(show: Boolean) {
        showEditMember.update { show }
    }

    fun searchGroupByCondition(input: GroupSearchData) {
        repository.updateGroupSearchCache(input)
        viewModelScope.launch {
            _isLoading.update { true }
            val gson = Gson()
            var jsonObject = JsonObject()
            jsonObject.addProperty("name", input.name)
            jsonObject.addProperty("locationName", input.location)
            jsonObject.addProperty("time", input.time)
            jsonObject.addProperty("priceMin", input.priceMin)
            jsonObject.addProperty("priceMax", input.priceMax)
            jsonObject.addProperty("describe", input.describe)
            val result = CommonPost("$serverUrl/group/search", jsonObject.toString())
            jsonObject = gson.fromJson(result, JsonObject::class.java)
            val collectionType = object : TypeToken<List<GroupSearchResult>>() {}.type
            val list = gson.fromJson<List<GroupSearchResult>>(
                jsonObject.get("result").asString,
                collectionType
            )
            repository.updateSearchGroupResult(list ?: emptyList())
            _isLoading.update { false }
        }
    }

    val groupSearchCache = repository.searchCache
    fun createGroup(input: GroupCreateData) {
        viewModelScope.launch {
            val gson = Gson()
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", input.name)
            jsonObject.addProperty("location", input.location)
            jsonObject.addProperty("time", input.time)
            jsonObject.addProperty("priceMin", input.priceMin)
            jsonObject.addProperty("priceMax", input.priceMax)
            jsonObject.addProperty("isPublic", input.isPublic)
            jsonObject.addProperty("describe", input.describe)
            val result = gson.fromJson(
                CommonPost("$serverUrl/group/create", jsonObject.toString()),
                JsonObject::class.java
            )
            joinGroup(result.get("groupId").toString(), getUserName())
        }
    }

    fun joinGroup(groupId: String, username: String) {
        viewModelScope.launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("groupId", groupId)
            jsonObject.addProperty("username", username)
            CommonPost("$serverUrl/group/join", jsonObject.toString())
            getGroupChatList(username)
        }
    }

    val selectSearchResult = repository.selectSearchResult
    fun updateSelectSearchResult(input: GroupSearchResult) {
        repository.updateSelectSearchResult(input)
    }

    val groupChatHistory = repository.groupChatHistory
    fun getGroupChatHistory(groupRoomId: Int) {
        viewModelScope.launch {
            _isLoading.update { true }
            val gson = Gson()
            var jsonObject = JsonObject()
            jsonObject.addProperty("id", "$groupRoomId")
            val result = CommonPost("$serverUrl/group/chat/get", jsonObject.toString())
            jsonObject = gson.fromJson(result, JsonObject::class.java)
            val collectionType = object : TypeToken<List<GroupChatHistory>>() {}.type
            val list = gson.fromJson<List<GroupChatHistory>>(
                jsonObject.get("result").asString,
                collectionType
            ) ?: emptyList()
            repository.updateGroupChatHistory(list)
            _isLoading.update { false }

        }
    }

    fun clearChatHistory() {
        repository.updateGroupChatHistory(emptyList())
        repository.updateGroupChatAvatar(emptyList())
    }

    fun updateGroupChat() {
        getGroupChatHistory(_nowChatRoomId.value)
    }

    //    fun sendMessage(chatInput: String) {
//        viewModelScope.launch {
//            val jsonObject = JsonObject()
//            jsonObject.addProperty("groupId", "${_nowChatRoomId.value}")
//            jsonObject.addProperty("username", getUserName())
//            jsonObject.addProperty("message", chatInput)
//            CommonPost("$serverUrl/group/chat/send", jsonObject.toString())
//            getGroupChatHistory(_nowChatRoomId.value)
//        }
//    }
    fun sendGroupMessage(chatInput: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("groupId", "${_nowChatRoomId.value}")
        jsonObject.addProperty("username", getUserName())
        jsonObject.addProperty("message", chatInput)
        sendGroupFcm(chatInput, jsonObject.toString())
    }

    /** 發送群組FCM */
    private fun sendGroupFcm(chatInput: String, groupChatJson: String) {
        viewModelScope.launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("title", _chatRoom.value.name)
            jsonObject.addProperty("body", chatInput)
            jsonObject.addProperty("data", groupChatJson)
            CommonPost("$serverUrl/group/chat/send", jsonObject.toString())
            getGroupChatHistory(_nowChatRoomId.value)
        }
    }

    fun getTokenSendServer() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { token ->
                    // 複製token到Firebase的Cloud Messaging測試區
                    //Log.d("qq", "token: $token")
                    sendTokenToServer(token)
                }
            }
        }
    }

    /** 將token送到server */
    private fun sendTokenToServer(token: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("username", getUserName())
        jsonObject.addProperty("fcmToken", token)
        //Log.d("qq",jsonObject.toString())
        viewModelScope.launch {
            CommonPost("$serverUrl/fcm/register", jsonObject.toString())
            //Log.d("qq", "sendTokenToServer: $result")
        }
    }

    fun leaveGroup() {
        viewModelScope.launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("groupId", "${_nowChatRoomId.value}")
            jsonObject.addProperty("username", getUserName())
            CommonPost("$serverUrl/group/leave", jsonObject.toString())
            getGroupChatList(getUserName())
        }
    }

    fun getAvatarImageInGroupChat(groupRoomId: Int) {
        viewModelScope.launch {
            _isAvatarLoading.update { true }
            val gson = Gson()
            var jsonObject = JsonObject()
            jsonObject.addProperty("id", "$groupRoomId")
            val result = CommonPost("$serverUrl/group/avatar", jsonObject.toString())
            jsonObject = gson.fromJson(result, JsonObject::class.java)
            val collectionType = object : TypeToken<List<GroupChatImage>>() {}.type
            val list = gson.fromJson<List<GroupChatImage>>(
                jsonObject.get("result").asString,
                collectionType
            ) ?: emptyList()
            val dataList = emptyList<GroupChatImage>().toMutableList()
            list.forEach {
                dataList.add(
                    GroupChatImage(
                        it.username,
                        it.profileImage,
                        userVM?.decodeBase64ToBitmap(it.profileImage)
                    )
                )
            }
            //Log.d("qq",dataList.toString())
            repository.updateGroupChatAvatar(dataList)
            _isAvatarLoading.update { false }
        }
    }

    val groupChatAvatar = repository.groupChatAvatar
    val restaurantList = repository.restaurantList
    fun getRestaurantList() {
        viewModelScope.launch {
            val gson = Gson()
            val result = CommonPost("$serverUrl/group/restaurant", "")
            val jsonObject = gson.fromJson(result, JsonObject::class.java)
            val collectionType = object : TypeToken<List<GroupRestaurantData>>() {}.type
            val list = gson.fromJson<List<GroupRestaurantData>>(
                jsonObject.get("result").asString,
                collectionType
            ) ?: emptyList()
            repository.updateRestaurantList(list)
        }
    }

    fun getRestaurantNameById(restaurantId: Int): String {
        if (repository.restaurantList.value.isEmpty()) {
            return ""
        }
        return repository.restaurantList.value[restaurantId - 1].restaurantName
    }

    val restaurantReview = repository.restaurantReview
    private fun updateRestaurantReview(input: GroupReviewData) {
        repository.updateRestaurantList(input)
    }

    fun getRestaurantReview(location: Int) {
        viewModelScope.launch {
            val gson = Gson()
            var jsonObject = JsonObject()
            jsonObject.addProperty("restaurantId", "$location")
            jsonObject.addProperty("reviewerNickname", getUserName())
            val result = CommonPost("$serverUrl/group/review", jsonObject.toString())
            jsonObject = gson.fromJson(result, JsonObject::class.java)
            val collectionType = object : TypeToken<GroupReviewData>() {}.type
            val data =
                gson.fromJson<GroupReviewData>(jsonObject.get("result").asString, collectionType)
                    ?: GroupReviewData()
            updateRestaurantReview(data)
        }
    }

    fun sendRestaurantReview(location: Int, stars: Int, comment: String) {
        viewModelScope.launch {
            val jsonObject = JsonObject()
            jsonObject.addProperty("reviewerNickname", getUserName())
            jsonObject.addProperty("restaurantId", "$location")
            jsonObject.addProperty("rating", "$stars")
            jsonObject.addProperty("comments", comment)
            jsonObject.addProperty(
                "reviewDate", LocalDateTime.now(ZoneId.of("UTC+8"))
                    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss"))
            )
            CommonPost("$serverUrl/group/review/send", jsonObject.toString())
            getRestaurantReview(location)
        }
    }
}