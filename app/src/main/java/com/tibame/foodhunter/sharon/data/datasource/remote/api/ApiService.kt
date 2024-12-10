package com.tibame.foodhunter.sharon.data.datasource.remote.api

import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NoteApiService {
    @GET("api/members/{memberId}/notes")
    // Response是http狀態響應
    suspend fun getNotes(
        @Path("memberId") memberId: String
    ) : Response<List<NoteResponse>>  // Response的庫要使用retrofit2
}