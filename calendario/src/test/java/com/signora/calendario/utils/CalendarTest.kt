package com.signora.calendario.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class CalendarTest {
    @Test
    fun `when calculateMonthTable is called, should get range 2022-12-26 to 2023-2-5`() {
        assertArrayEquals(
            calculateMonthTable(LocalDate.of(2023, 1, 1)).toTypedArray(),
            Array(7 * 6) {
                LocalDate.of(2022, 12, 26).plusDays(it.toLong())
            }
        )
    }
}