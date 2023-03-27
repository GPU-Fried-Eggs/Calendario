package com.signora.calendario.utils

import java.time.LocalDate

internal fun calculateMonthTable(startDate: LocalDate, offset: Int = 0): List<LocalDate> {
    val monthFirstDate = startDate.run {
        when {
            offset > 0 -> minusMonths(1)
            offset < 0 -> plusMonths(1)
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