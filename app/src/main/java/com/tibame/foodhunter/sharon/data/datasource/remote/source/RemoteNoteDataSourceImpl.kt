package com.tibame.foodhunter.sharon.data.datasource.remote.source

import com.tibame.foodhunter.sharon.data.datasource.base.NoteDataSource
import com.tibame.foodhunter.sharon.data.datasource.remote.api.NoteApiService
import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import com.tibame.foodhunter.sharon.domain.error.DataError
import com.tibame.foodhunter.sharon.domain.error.Result
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoteNoteDataSourceImpl @Inject constructor(
    private val noteApiService: NoteApiService
) : NoteDataSource {
    override suspend fun getNotes(memberId: String):Result<List<NoteDto>, DataError.Network> {
        return try {
            val noteList = noteApiService.getNotes(memberId)
            Result.Success(noteList)

        } catch (e: HttpException) {
            // 根據 HTTP 狀態碼返回對應的錯誤類型
            // HttpException 直接提供了狀態碼，使得錯誤處理更加直接
            when (e.code()) {
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }

        } catch (e: IOException) {
            // 處理網路連線問題，如斷網情況
            Result.Error(DataError.Network.NO_INTERNET)

        } catch (e: Exception) {
            // 捕獲所有其他未預期的錯誤
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}