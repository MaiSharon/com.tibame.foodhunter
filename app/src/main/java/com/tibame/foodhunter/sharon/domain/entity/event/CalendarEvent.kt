package com.tibame.foodhunter.sharon.domain.entity.event

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object CalendarEvent {
    private val _refreshTrigger = MutableStateFlow(false)
    val refreshTrigger = _refreshTrigger.asStateFlow()

    fun triggerRefresh() {
        _refreshTrigger.value = true
    }

    fun resetTrigger() {
        _refreshTrigger.value = false
    }
}