package com.signora.calendario.utils

import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

internal fun calculateMonthTable(yearMonth: YearMonth, offset: Long = 0): List<LocalDate> {
    val monthFirstDate = yearMonth.atDay(1).run {
        when {
            offset > 0 -> plusMonths(offset)
            offset < 0 -> minusMonths(abs(offset))
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

internal fun Pair<LocalDate, LocalDate>.formatNeighborWeek(range: Long = 1): List<Pair<LocalDate, LocalDate>> {
    val (headStartDay, headEndDay) = this.first.minusWeeks(range) to this.second.minusWeeks(range)
    return (0..2 * range).map { headStartDay.plusWeeks(it) to headEndDay.plusWeeks(it) }
}

internal fun YearMonth.formatNeighborMonth(range: Long = 1): List<YearMonth> {
    val headMonth = this.minusMonths(range)
    return (0..2 * range).map { headMonth.plusMonths(it) }
}