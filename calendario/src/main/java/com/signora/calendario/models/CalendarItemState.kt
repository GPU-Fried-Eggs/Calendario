package com.signora.calendario.models

import java.time.LocalDate

sealed interface CalendarItemState {
    object Selected : CalendarItemState

    object Today : CalendarItemState

    object Default : CalendarItemState

    companion object {
        fun getState(current: LocalDate, selectedDate: LocalDate?): CalendarItemState {
            return when (current) {
                LocalDate.now() -> Today
                selectedDate -> Selected
                else -> Default
            }
        }
    }
}