package com.tibame.foodhunter.core.data.networking

import com.google.gson.JsonParseException    // Gson 的基本異常類
import com.google.gson.JsonSyntaxException   // Gson 的語法異常類
import com.tibame.foodhunter.core.data.networking.model.ApiResponse
import com.tibame.foodhunter.core.domain.util.DataError
import com.tibame.foodhunter.core.domain.util.Result
import kotlinx.coroutines.ensureActive
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.coroutineContext
import javax.inject.Inject

// 處理各種不同類型的 API 響應
// 泛型函數 execute，接收一個異步函數作為參數
class NetworkCaller @Inject constructor() {
    suspend fun <T> execute(  // 協程函數
        apiCall: suspend () -> ApiResponse<T>  // suspend () -> 是無參數的異步函數
    ): Result<T, DataError.Network> {
        return try {
            val apiResponse = apiCall()

            // 處理業務邏輯狀態碼
            when (apiResponse.status) {
                in 200..299 -> {
                    apiResponse.data?.let {
                        Result.Success(it)
                    } ?: Result.Error(DataError.Network.PARSE_ERROR)
                }
                400 -> Result.Error(DataError.Network.BAD_REQUEST)
                404 -> Result.Error(DataError.Network.NOT_FOUND)
                in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }

        } catch (e: Exception) {
            Result.Error(
                when (e) {
                    is JsonSyntaxException, //  JSON 語法錯誤
                    is JsonParseException->
                        DataError.Network.PARSE_ERROR  //  JSON 解析相關錯誤
                    is IOException -> DataError.Network.NO_INTERNET
                    else -> {
                        // 確保協程仍然活躍
                        coroutineContext.ensureActive()
                        DataError.Network.UNKNOWN
                    }
                }
            )
        }
    }
}