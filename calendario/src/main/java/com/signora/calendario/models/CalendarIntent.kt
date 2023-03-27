package com.signora.calendario.models

import java.time.LocalDate
import java.time.YearMonth

private const val MAX_STEP = 2

sealed class CalendarIntent {
    class LoadNextDate(val yearMonth: YearMonth, val period: Period, val range: Int = MAX_STEP) : CalendarIntent()

    class SelectDate(val date: LocalDate) : CalendarIntent()

    object ExpandCalendar : CalendarIntent()

    object CollapseCalendar : CalendarIntent()
}