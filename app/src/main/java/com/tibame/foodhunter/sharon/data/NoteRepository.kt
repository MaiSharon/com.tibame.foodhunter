package com.tibame.foodhunter.sharon.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.tibame.foodhunter.core.data.remote.api.CommonPost
import com.tibame.foodhunter.core.data.remote.api.serverUrl
import com.tibame.foodhunter.sharon.domain.entity.CardContentType
import com.tibame.foodhunter.sharon.domain.entity.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 筆記資料存取層
 * 專注於與後端API的溝通和資料轉換
 */
class NoteRepository private constructor() {
    companion object {
        private const val TAG = "NoteRepository"
        private const val BASE_URL = serverUrl
        private const val API_PATH = "/api/note"
        val instance = NoteRepository()
    }


    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val gson = Gson()

    /**
     * 取得所有筆記
     * @return 成功回傳正數，失敗回傳-1
     */
    suspend fun getNotes(memberId: Int) {
        try {
            // 組合完整 URL
            val url = "$BASE_URL$API_PATH/getAllNotes"

            // 準備請求參數
            val requestBody = JsonObject().apply {
                addProperty("member_id", memberId)
            }

            // 發送請求
            val response = CommonPost(url, requestBody.toString())
            Log.d(TAG, "API回應: $response")



            // 有空再研究簡化
//            val type = object : TypeToken<List<NoteApiResponse>>() {}.type
//            val notesList = gson.fromJson(response, type)

            // 檢查是否有回應
            if (response.isNotEmpty()) {
                // 解析回應
                val jsonResponse = gson.fromJson(response, JsonObject::class.java)

                // 檢查是否有錯誤
                if (jsonResponse.has("error")) {
                    Log.e(TAG, "API返回錯誤: ${jsonResponse.get("error").asString}")
                    _notes.value = emptyList()
                    return
                }

                // 檢查 JSON 解析
                val notesArray = jsonResponse.getAsJsonArray("notes")
                Log.d(TAG, "解析到的筆記陣列: $notesArray")  // 看JSON陣列


                val notesList = notesArray.map { noteJson ->
                    val noteObj = noteJson.asJsonObject
                    Log.d(TAG, "單筆筆記JSON: $noteObj")
                    // 看單筆資料

                    // 取得筆記陣列
                    val apiResponse = NoteApiResponse(
                        note_id = noteObj.get("note_id").asInt,
                        title = noteObj.get("title").asString,
                        content = noteObj.get("content").asString,
                        restaurant_id = noteObj.get("restaurant_id").asInt,
                        member_id = noteObj.get("member_id").asInt,
                        selected_date = noteObj.get("selected_date").asString
                    )

                    Log.d(TAG, "轉換成 ApiResponse: $apiResponse")  // 看轉換結果
                    apiResponse.toNote()

                }
                // 按 note_id 排序 (升序)
                val sortedNotesList = notesList.sortedByDescending { it.selectedDate }

                // 更新 StateFlow
                _notes.value = sortedNotesList
                Log.d(TAG, "成功載入 ${sortedNotesList.size} 筆筆記")

            } else {
                Log.e(TAG, "API 無回應")
                _notes.value = emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "獲取筆記列表時發生錯誤", e)
            e.printStackTrace()
            _notes.value = emptyList()
        }
    }

    /**
     * 根據ID取得單一筆記
     */
    suspend fun getNoteById(memberId: Int): Note? {
        val url = "$BASE_URL$API_PATH/getNoteById"

        try {
            // 準備請求參數
            val requestBody = JsonObject().apply {
                addProperty("note_id", memberId)
            }

            // 發送請求
            val response = CommonPost(url, requestBody.toString())
            Log.d(TAG, "API回應: $response")

            if (response.isNotEmpty()) {
                // 解析回應
                val jsonResponse = gson.fromJson(response, JsonObject::class.java)

                // 檢查是否有錯誤
                if (jsonResponse.has("error") || jsonResponse.has("NotFind")) {
                    Log.d(TAG, "回應含錯誤: $jsonResponse")
                    return null
                }

                // 從 JSON 轉換日期字串到 Date
                val dateString = jsonResponse.get("selected_date").asString
                Log.e(TAG, "查看日期: $dateString")


                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selectedDate = try {
                    dateFormat.parse(dateString) ?: Date()
                } catch (e: Exception) {
                    Log.e(TAG, "日期解析錯誤: $dateString", e)
                    Date()  // 解析失敗時使用當前日期
                }

                // 轉換成Note物件
                return Note(
                    noteId = jsonResponse.get("note_id").asInt,
                    title = jsonResponse.get("title").asString,
                    content = jsonResponse.get("content").asString,
                    type = CardContentType.NOTE,
                    selectedDate = selectedDate,
                    date = formatDate(jsonResponse.get("selected_date").asString),
                    day = formatDay(jsonResponse.get("selected_date").asString),
                    restaurantName = null,
                    imageResId = null,
                    memberId = jsonResponse.get("member_id").asInt,
                ).also {
                    Log.d(TAG, "成功建立Note物件: $it")
                }
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "獲取筆記時發生錯誤", e)
            return null
        }
    }

    /**
     * 根據ID取得單一筆記 (本地測試版本)
     */
    suspend fun getNoteById1(noteId: Int): Note? {
        // 先嘗試從本地列表查找
        val localNote = _notes.value.find { it.noteId == noteId }
        if (localNote != null) {
            Log.d(TAG, "從本地找到筆記: $localNote")
            return localNote
        }

        // 如果本地找不到，且不想使用API，直接返回null
        Log.d(TAG, "本地找不到筆記ID: $noteId")
        return null

        // 先註解掉 API 呼叫的部分
        /*
        val url = "$BASE_URL$API_PATH/getNoteById"
        try {
            // API 呼叫的程式碼...
        } catch (e: Exception) {
            Log.e(TAG, "獲取筆記時發生錯誤", e)
            return null
        }
        */
    }


    /**
     * 新增筆記
     * @return 成功回傳正數，失敗回傳-1
     */
    suspend fun addNote(
        title: String,
        content: String,
        restaurantId: Int,
        selectedDate: Date,
        memberId: Int
    ): Int {
        val url = "$BASE_URL$API_PATH/create"

        // 轉成 "yyyy-MM-dd" 給後端
        val backendDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(selectedDate)

        try {
            // 準備請求參數
            val requestBody = JsonObject().apply {
                addProperty("title", title)
                addProperty("content", content)
                addProperty("restaurantId", restaurantId)
                addProperty("selectedDate", backendDateStr)
                addProperty("memberId", memberId)
            }

            Log.d(TAG, "新增筆記請求：$requestBody")

            val response = CommonPost(url, requestBody.toString())
            Log.d(TAG, "新增筆記回應：$response")

            if (response.isNotEmpty()) {
                val jsonResponse = gson.fromJson(response, JsonObject::class.java)

                // 檢查回應中的 result
                val result = jsonResponse.get("result")?.asBoolean ?: false

                return if (result) {

                    getNotes(memberId)
                    Log.d(TAG, "新增筆記成功")
                    1  // 成功返回1
                } else {
                    val errMsg = jsonResponse.get("errMsg")?.asString ?: "未知錯誤"
                    Log.e(TAG, "新增筆記失敗: $errMsg")
                    return -1
                }
            } else {
                Log.e(TAG, "API 回應為空")
                return -1
            }
        } catch (e: Exception) {
            Log.e(TAG, "新增筆記時發生錯誤", e)
            return -1
        }
    }

    /**
     * API回應的資料結構，用於匹配後端回傳的JSON格式
     * 主要用於資料傳輸和解析
     */
    data class NoteApiResponse(
        val note_id: Int,        // 後端回傳ID是數字型態
        val title: String,
        val content: String,
        val restaurant_id: Int,  // 後端回傳餐廳ID
        val member_id: Int,      // 後端回傳會員ID
        val selected_date: String, // 後端回傳的日期格式 "2024-10-14 00:00:00.0"
    )

    // API 資料轉換方法通常使用 "to" 前綴
    private fun NoteApiResponse.toNote(): Note {
        // 字串轉 Date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(selected_date) ?: Date()
        return Note(
            noteId = note_id,
            title = title,
            content = content,
            type = CardContentType.NOTE,
            selectedDate = date,
            date = formatDate(selected_date),
            day = formatDay(selected_date),
            restaurantName = null,
            imageResId = null,
            memberId = member_id
        )
    }

    suspend fun deleteNoteById(noteId: Int,): Int {
        val url = "$BASE_URL$API_PATH/deleteNoteById"
        val requestBody = JsonObject().apply {
            addProperty("note_id", noteId)
        }

        return try {
            val response = CommonPost(url, requestBody.toString())
            Log.d(TAG, "刪除筆記回應：$response")

            if (response.isNotEmpty()) {
                val jsonResponse = gson.fromJson(response, JsonObject::class.java)
                val resultStr = jsonResponse.get("result")?.asString ?: ""

                if (resultStr.contains("刪除成功")) {
                    Log.d(TAG, "刪除筆記成功，ID: $noteId")

                    refreshNotes()
                    _notes.value = _notes.value.filter { it.noteId != noteId }

                    1  // 成功返回1
                } else {
                    val errMsg = jsonResponse.get("error")?.asString ?: "未知錯誤"
                    Log.e(TAG, "刪除筆記失敗: $errMsg")
                    -1  // 刪除失敗
                }
            } else {
                Log.e(TAG, "API 回應為空")
                -1  // 空回應時的錯誤碼
            }
        } catch (e: Exception) {
            Log.e(TAG, "刪除筆記時發生錯誤", e)
            -1  // 例外錯誤時的錯誤碼
        }
    }


    suspend fun updateNote(
        noteId: Int,
        title: String,
        content: String,
        restaurantId: Int,
        selectedDate: Date,
    ): Int {
        val url = "$BASE_URL$API_PATH/update"

        val backendDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(selectedDate)

        val requestBody = JsonObject().apply {
            addProperty("noteId", noteId)
            addProperty("title", title)
            addProperty("content", content)
            addProperty("restaurantId", restaurantId)
            addProperty("selectedDate", backendDateStr)
        }

        return try {
            val response = CommonPost(url, requestBody.toString())
            // 使用 Gson 將 JSON 字符串轉換為 JsonObject
            val jsonResponse = JsonParser.parseString(response).asJsonObject
            val backendResult = jsonResponse.get("result").asBoolean
            Log.e(TAG, "更新筆記結果 $backendResult")

            if (backendResult) {
                refreshNotes()
                1
            } else {
                -1
            }
        } catch (e: Exception) {
            Log.e(TAG, "更新筆記失敗", e)
            -1
        }
    }

    // 重整資料
    private suspend fun refreshNotes() {
        _notes.value = emptyList()
//        getNotes()  // 重新取得資料
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

}