package com.tibame.foodhunter.sharon.data.datasource.remote.api

import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import retrofit2.http.GET
import retrofit2.http.Path

interface NoteApiService {
    @GET("members/{memberId}/notes")
    suspend fun getNotes(
        @Path("memberId") memberId: String
    ): List<NoteDto>
}