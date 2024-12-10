package com.tibame.foodhunter.sharon.internal.util

import java.time.LocalDate
import java.time.YearMonth

/**
 * CalendarDataSource 負責提供日曆的資料來源
 * 主要根據傳入的 YearMonth 來生成每個月的日期資料
 */
class CalendarDataSource {

    /**
     * getDates 函數會根據傳入的 yearMonth 來生成一個日期列表 (List)
     * 每個日期會以 CalendarUiState.Date 的形式返回
     *
     * @param yearMonth YearMonth: 代表某個年份和月份
     * @return List<CalendarUiState.Date>: 返回包含該月份日期的列表
     */
    fun getDates(yearMonth: YearMonth): List<CalendarUiState.Date> {
        return yearMonth.getDayOfMonthStartingFromMonday()
            .map { date ->
                CalendarUiState.Date(
                    dayOfMonth = if (date.monthValue == yearMonth.monthValue) {
                        "${date.dayOfMonth}"  // 顯示當月的日期
                    } else {
                        ""
                    },
                    isSelected = date.isEqual(LocalDate.now()) && date.monthValue == yearMonth.monthValue,
                    month = date.monthValue,
                    year = date.year
                )
            }
    }
}