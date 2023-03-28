package com.signora.calendario.models

import java.time.LocalDate
import java.time.YearMonth

sealed class CalendarIntent {
    class LoadDate(val monthWeek: Pair<YearMonth, Pair<LocalDate, LocalDate>?>, val range: Int = 1, val period: CalendarPeriod = CalendarPeriod.MONTH) : CalendarIntent()

    class SelectDate(val date: LocalDate) : CalendarIntent()

    object ExpandCalendar : CalendarIntent()

    object CollapseCalendar : CalendarIntent()
}