package com.signora.calendario.models

import com.signora.calendario.utils.getRemainingDatesInWeek
import com.signora.calendario.utils.getWeekStartDate
import com.signora.calendario.utils.getYearMonth
import java.time.LocalDate
import java.time.YearMonth

data class Week(val start: LocalDate, val end: LocalDate) {
    val crossMonth: Pair<YearMonth, YearMonth>
        get() = Pair(start.getYearMonth(), end.getYearMonth())

    val isCrossMonth: Boolean
        get() = start.month != end.month

    fun plusMonths(monthsToAdd: Long) {
        start.plusMonths(monthsToAdd)
        end.plusMonths(monthsToAdd)
    }

    fun plusWeeks(weeksToAdd: Long) {
        start.plusWeeks(weeksToAdd)
        end.plusWeeks(weeksToAdd)
    }

    companion object {
        fun now(): Week {
            val localDate = LocalDate.now()
            return Week(
                start = localDate.getWeekStartDate(),
                end = localDate.getRemainingDatesInWeek().last()
            )
        }

        fun of(yearMonth: YearMonth): Week {
            val localDate = yearMonth.atDay(1)
            return Week(
                start = localDate.getWeekStartDate(),
                end = localDate.getRemainingDatesInWeek().last()
            )
        }
    }
}