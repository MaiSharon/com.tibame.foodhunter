package com.tibame.foodhunter.sharon.data.datasource.remote.model

import com.google.gson.annotations.SerializedName


data class NoteDto(
    @SerializedName("note_id") val noteId: Int,
    val title: String,
    val content: String,
    @SerializedName("restaurant_id") val restaurantId: Int,
    @SerializedName("member_id") val memberId: Int,
    @SerializedName("selected_date") val selectedDate: String
)