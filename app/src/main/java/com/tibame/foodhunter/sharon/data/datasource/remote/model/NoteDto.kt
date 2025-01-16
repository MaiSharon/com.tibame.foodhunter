package com.tibame.foodhunter.sharon.data.datasource.remote.model

import com.google.gson.annotations.SerializedName


data class NoteDto(
    @SerializedName("noteId") val noteId: Int,
    val title: String,
    val content: String,
    @SerializedName("restaurantId") val restaurantId: Int,
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("selectedDate") val selectedDate: String
)
