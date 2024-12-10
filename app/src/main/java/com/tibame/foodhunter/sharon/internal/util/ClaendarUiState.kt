package com.tibame.foodhunter.sharon.internal.util

import java.time.YearMonth


/**
 * CalendarUiState 用來儲存日曆的 UI 狀態
 * 包含當前的年份和月份 (yearMonth) 以及每一天的日期列表 (dates)
 */
data class CalendarUiState(
    val yearMonth: YearMonth,   // 當前顯示的年與月
    val dates: List<Date>       // 當月的日期列表，每個日期用 Date 類別表示

) {
    companion object {
        /**
         * Init 是初始狀態，當還沒有載入資料時顯示當前的年份和月份，但日期列表為空
         * 可以作為預設值來使用
         */
        val Init = CalendarUiState(
            yearMonth = YearMonth.now(),  // 初始化為當前年與月
            dates = emptyList()           // 日期列表初始化為空
        )
    }

    /**
     * 表示日曆中的某一天。
     *
     * 此類提供日曆視圖中單個日期的基本結構，包含該日期的具體天數、月份、年份、選中狀態及與該日期相關的書籍。
     *
     * 屬性:
     * - `dayOfMonth` (String): 顯示當月的某天 (例如 "1", "2", "15")。
     * - `month` (Int): 月份，以整數表示 (1 表示 1 月，12 表示 12 月)。
     * - `year` (Int): 該日期的年份。
     * - `isSelected` (Boolean): 指示該日期是否為選中狀態。
     * - `hasBook` (Boolean): 標記該日期是否有相關書籍。
     * - `books` (List<Book>): 與該日期相關的書籍列表。
     *
     * 使用場景:
     * 此資料類用於日曆 UI 中表示每一天，並顯示如該日期的書籍排程等相關信息。
     *
     * 範例:
     * ```
     * val today = Date(
     *     dayOfMonth = "15",
     *     month = 10,
     *     year = 2023,
     *     isSelected = true,
     *     hasBook = true,
     *     books = listOf(Book("Sample Book"))
     * )
     * ```
     *
     * Companion Object:
     * - `Empty`: 用於表示非當月的空白日期或作為占位符的空 Date 物件。
     */
    data class Date(
        val dayOfMonth: String,     // 當天的日期（字串形式，例如 "1", "15"）
        val month: Int,             // 月份（1 代表一月，12 代表十二月）
        val year: Int,              // 年份
        val isSelected: Boolean,    // 標記此日期是否在 UI 中為選中狀態
        val hasCard: Boolean = false,  // 若此日期有相關活動則為 true
        val items: List<Any> = emptyList()  // 改為可以存放 Note 和 Group
    ) {
        companion object {
            /**
             * 靜態空 Date 實例，用於表示佔位符或當日期在顯示月份範圍外時使用。
             */
            val Empty = Date("", 0, 0, false)  // 預設空日期，未選中
        }
    }
}