package com.signora.calendario.utils

import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

internal fun calculateMonthTable(yearMonth: YearMonth, offset: Int = 0): List<LocalDate> {
    val monthFirstDate = yearMonth.atDay(1).run {
        when {
            offset > 0 -> plusMonths(offset.toLong())
            offset < 0 -> minusMonths(abs(offset).toLong())
            else -> this
        }
    }
    val monthLastDate = monthFirstDate.plusMonths(1).minusDays(1)
    val weekBeginningDate = monthFirstDate.getWeekStartDate()
    return if (weekBeginningDate != monthFirstDate) {
        weekBeginningDate.getRemainingDatesInMonth()
    } else {
        emptyList()
    } + monthFirstDate.getNextDates(monthFirstDate.month.length(monthFirstDate.isLeapYear)) +
        monthLastDate.getRemainingDatesInWeek()
}