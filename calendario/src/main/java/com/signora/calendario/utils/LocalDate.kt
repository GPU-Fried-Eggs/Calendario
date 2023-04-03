package com.signora.calendario.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

internal fun LocalDate.getNextDates(count: Int): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    repeat(count) {
        dates.add(this.plusDays(it.toLong()))
    }
    return dates
}

internal fun LocalDate.getWeekStartDate(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
    var date = this
    while (date.dayOfWeek != weekStartDay) {
        date = date.minusDays(1)
    }
    return date
}

internal fun LocalDate.getWeekEndDate(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
    var date = this
    while (date.dayOfWeek != weekStartDay.plus(6)) {
        date = date.plusDays(1)
    }
    return date
}

internal fun LocalDate.getRemainingDatesInWeek(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var date = this.plusDays(1)
    while (date.dayOfWeek != weekStartDay) {
        dates.add(date)
        date = date.plusDays(1)
    }
    return dates
}

internal fun LocalDate.getWeekRange(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): Pair<LocalDate, LocalDate> {
    return this.getWeekStartDate(weekStartDay) to this.getWeekEndDate(weekStartDay)
}

internal fun LocalDate.getMonthStartDate(): LocalDate {
    return LocalDate.of(this.year, this.month, 1)
}

internal fun LocalDate.getMonthEndDate(): LocalDate {
    return YearMonth.of(this.year, this.month).atEndOfMonth()
}

internal fun LocalDate.getRemainingDatesInMonth(): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    repeat(this.month.length(this.isLeapYear) - this.dayOfMonth + 1) {
        dates.add(this.plusDays(it.toLong()))
    }
    return dates
}

internal fun LocalDate.getYearMonth(): YearMonth {
    return YearMonth.of(this.year, this.month)
}