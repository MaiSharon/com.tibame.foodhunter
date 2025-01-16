package com.tibame.foodhunter.sharon.data.datasource.remote.api

import com.tibame.foodhunter.core.data.networking.model.ApiResponse
import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NoteApiService {
    @GET("api/v1/notes")
    suspend fun getNotes(): ApiResponse<List<NoteDto>>

    @GET("api/v1/notes/{noteId}")
    suspend fun getNote(
        @Path("noteId") noteId: Int
    ): ApiResponse<NoteDto>

    @POST("api/v1/notes")
    suspend fun createNote(
        @Body noteDto: NoteDto
    ): ApiResponse<NoteDto>

    @PUT("api/v1/notes/{noteId}")  // 注意：我修正了路徑中缺少的 "/"
    suspend fun updateNote(
        @Path("noteId") noteId: Int,
        @Body noteDto: NoteDto
    ): ApiResponse<NoteDto>

    @DELETE("api/v1/notes/{noteId}")
    suspend fun deleteNote(
        @Path("noteId") noteId: Int
    ): ApiResponse<Unit>  // DELETE 通常不需要回傳資料，所以用 Unit
}