package com.signora.calendario.models

import java.time.LocalDate

sealed interface CalendarIntent {
    data class LoadDate(val date: Any, val range: Int = 1, val period: CalendarPeriod = CalendarPeriod.MONTH) : CalendarIntent

    data class SelectDate(val date: LocalDate) : CalendarIntent

    object ExpandCalendar : CalendarIntent

    object CollapseCalendar : CalendarIntent
}