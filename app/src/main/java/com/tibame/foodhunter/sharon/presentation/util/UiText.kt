package com.tibame.foodhunter.sharon.presentation.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


/**
 * UiText 類是一個封裝UI文本的密封類，用於處理兩種不同類型的文本:
 * 1. 動態生成的字符串
 * 2. 從資源文件中讀取的字符串（支援多語言）
 */
sealed class UiText {
    /**
     * 用於處理動態生成的字符串，例如:
     * - 包含用戶名的訊息
     * - 運行時生成的錯誤訊息
     *
     * 使用範例:
     * UiText.DynamicString("歡迎回來，${userName}")
     */
    data class DynamicString(val value: String) : UiText()

    /**
     * 用於從資源文件(res/values/strings.xml)中讀取的字符串
     * @param id 字符串資源的ID (R.string.xxx)
     * @param args 用於替換字符串中佔位符的參數數組
     *
     * 使用範例:
     * UiText.StringResource(R.string.welcome_message, arrayOf(userName))
     * 其中 strings.xml 可能包含: <string name="welcome_message">歡迎回來，%s</string>
     */
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText()

    /**
     * 在 Composable 環境中將 UiText 轉換為字符串
     *
     * 使用範例:
     * Text(text = myUiText.asString())
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> LocalContext.current.getString(id, *args)
        }
    }

    /**
     * 在非 Composable 環境中將 UiText 轉換為字符串
     * @param context Android Context 對象，用於訪問資源
     *
     * 使用範例:
     * val message = myUiText.asString(context)
     * Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
     */
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args)
        }
    }
}

// 使用範例：
/*
// 1. 動態字符串
val dynamicText = UiText.DynamicString("Hello, John")

// 2. 資源字符串
val resourceText = UiText.StringResource(
   R.string.welcome_message,
   arrayOf("John")
)

// 3. 在 Composable 中使用
@Composable
fun MyScreen(text: UiText) {
   Text(text = text.asString())
}

// 4. 在普通函數中使用
fun showToast(context: Context, text: UiText) {
   Toast.makeText(context, text.asString(context), Toast.LENGTH_SHORT).show()
}
*/