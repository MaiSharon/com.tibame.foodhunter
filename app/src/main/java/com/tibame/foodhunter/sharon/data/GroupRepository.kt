package com.tibame.foodhunter.sharon.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import com.tibame.foodhunter.sharon.domain.entity.CardContentType
import com.tibame.foodhunter.sharon.domain.entity.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 群組資料存取層
 */
class GroupRepository private constructor() {
    companion object {
        private const val TAG = "GroupRepository"
        private const val BASE_URL = serverUrl
        private const val API_PATH = "/api/group"
        val instance = GroupRepository()
    }

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val gson = Gson()

    suspend fun getGroups(memberId: Int) {
        try {
            val url = "$BASE_URL$API_PATH/getAllGroups"

            val requestBody = JsonObject().apply {
                addProperty("member_id", memberId)
            }

            val response = CommonPost(url, requestBody.toString())
            Log.d(TAG, "API回應: $response")

            if (response.isNotEmpty()) {
                val jsonResponse = gson.fromJson(response, JsonObject::class.java)

                if (jsonResponse.has("error")) {
                    Log.e(TAG, "API返回錯誤: ${jsonResponse.get("error").asString}")
                    _groups.value = emptyList()
                    return
                }

                val groupsArray = jsonResponse.getAsJsonArray("groups")
                Log.d(TAG, "解析到的群組陣列: $groupsArray")

                val groupsList = groupsArray.map { groupJson ->
                    val groupObj = groupJson.asJsonObject
                    Log.d(TAG, "單個群組JSON: $groupObj")

                    val apiResponse = GroupApiResponse(
                        group_id = groupObj.get("group_id").asInt,
                        name = groupObj.get("name").asString,
                        restaurant_name = groupObj.get("restaurant_name")?.asString,
                        address = groupObj.get("address")?.asString,
                        is_public = groupObj.get("is_public").asInt,
                        time = groupObj.get("time").asString,
                        member_id = groupObj.get("member_id").asInt
                    )

                    Log.d(TAG, "轉換成 ApiResponse: $apiResponse")
                    apiResponse.toGroup()
                }

                val sortedGroupsList = groupsList.sortedByDescending { it.groupDate }
                _groups.value = sortedGroupsList
                Log.d(TAG, "成功載入 ${sortedGroupsList.size} 個群組")

            } else {
                Log.e(TAG, "API 無回應")
                _groups.value = emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "獲取群組列表時發生錯誤", e)
            e.printStackTrace()
            _groups.value = emptyList()
        }
    }

    /**
     * API回應的資料結構
     */
    data class GroupApiResponse(
        val group_id: Int,
        val name: String,
        val restaurant_name: String?,
        val address: String?,
        val is_public: Int,
        val time: String,
        val member_id: Int
    )

    private fun GroupApiResponse.toGroup(): Group {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = try {
            dateFormat.parse(time) ?: Date()
        } catch (e: Exception) {
            Log.e(TAG, "日期解析錯誤: $time", e)
            Date()
        }

        return Group(
            groupId = group_id,
            groupName = name,
            restaurantName = restaurant_name,
            restaurantAddress = address,
            isPublic = is_public,
            groupDate = date,
            type = CardContentType.GROUP,
            date = formatDate(time),
            day = formatDay(time),
            memberId = member_id
        )
    }

    /**
     * 格式化日期 (MM/dd)
     */
    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        try {
            val date = inputFormat.parse(dateString)
                ?: throw IllegalArgumentException("無法解析日期: $dateString")
            return outputFormat.format(date)
        } catch (e: Exception) {
            Log.e(TAG, "日期格式化錯誤: $dateString", e)
            throw IllegalArgumentException("日期格式錯誤: $dateString", e)
        }
    }

    /**
     * 格式化星期幾
     */
    private fun formatDay(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE", Locale.CHINESE)
        try {
            val date = inputFormat.parse(dateString)
                ?: throw IllegalArgumentException("無法解析日期: $dateString")

            return "星期" + outputFormat.format(date).substring(2, 3)
        } catch (e: Exception) {
            Log.e(TAG, "日期格式化錯誤: $dateString", e)
            throw IllegalArgumentException("日期格式錯誤: $dateString", e)
        }
    }

    private suspend fun refreshGroups() {
        _groups.value = emptyList()
    }
}
