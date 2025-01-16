package com.tibame.foodhunter.core.data.networking

object SessionManager {
    private var sessionCookie: String? = null

    fun setSessionCookie(cookie: String) {
        sessionCookie = cookie
    }

    fun getSessionCookie(): String? = sessionCookie

    fun clearSession() {
        sessionCookie = null
    }
}