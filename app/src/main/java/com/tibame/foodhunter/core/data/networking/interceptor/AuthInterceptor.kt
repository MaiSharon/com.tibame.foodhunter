package com.tibame.foodhunter.core.data.networking.interceptor

import com.tibame.foodhunter.core.data.networking.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

// AuthInterceptor.kt
/**
 * HTTP 請求攔截器，負責自動在每個請求中加入驗證 token
 * JWT用
 */
class AuthInterceptor : Interceptor {
    /**
     * 攔截 HTTP 請求並加入認證標頭
     * @param chain 請求鏈，包含原始請求資訊
     * @return 加入認證標頭後的回應
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. 獲取原始請求
        val request = chain.request()

        // 2. 從 SessionManager 取得使用者 cookie
        val sessionCookie = SessionManager.getSessionCookie()

        // 3. 如果有 cookie，建立新的請求並加入認證標頭
        return if (!sessionCookie.isNullOrEmpty()) {
            // 使用 Builder 模式建立新請求，並加入 cookie 標頭
            val newRequest = request.newBuilder()
                .header("Cookie", sessionCookie)
                .build()
            // 發送修改後的請求
            chain.proceed(newRequest)
        } else {
            // 如果沒有 token，直接發送原始請求
            chain.proceed(request)
        }
    }
}