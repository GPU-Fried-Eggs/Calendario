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

internal fun Pair<LocalDate, LocalDate>.formatNeighborWeek(range: Int = 1): Array<Pair<LocalDate, LocalDate>> {
    val (headStartDay, headEndDay) = this.first.minusWeeks(range.toLong()) to this.second.minusWeeks(range.toLong())
    return (0..2 * range).map { headStartDay.plusWeeks(it.toLong()) to headEndDay.plusWeeks(it.toLong()) }.toTypedArray()
}

internal fun YearMonth.formatNeighborMonth(range: Int = 1): Array<YearMonth> {
    val headMonth = this.minusMonths(range.toLong())
    return (0..2 * range).map { headMonth.plusMonths(it.toLong()) }.toTypedArray()
}