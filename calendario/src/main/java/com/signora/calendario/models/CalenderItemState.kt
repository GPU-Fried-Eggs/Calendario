package com.signora.calendario.models

import java.time.LocalDate

sealed class CalenderItemState {
    object Selected : CalenderItemState()

    object Today : CalenderItemState()

    object Default : CalenderItemState()

    companion object {
        fun getState(current: LocalDate, selectedDate: LocalDate?): CalenderItemState {
            return when (current) {
                LocalDate.now() -> Today
                selectedDate -> Selected
                else -> Default
            }
        }
    }
}