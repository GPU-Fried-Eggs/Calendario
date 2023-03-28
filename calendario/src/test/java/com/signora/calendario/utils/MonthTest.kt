package com.signora.calendario.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class MonthTest {
    @Test
    fun `when calculateMonthTable is called, should get range 2022-12-26 to 2023-2-5`() {
        assertArrayEquals(
            calculateMonthTable(YearMonth.of(2023, 1)).toTypedArray(),
            Array(7 * 6) {
                LocalDate.of(2022, 12, 26).plusDays(it.toLong())
            }
        )
    }

    @Test
    fun `when calculateMonthTable with positive offset is called, should get range 2023-1-30 to 2023-3-5`() {
        assertArrayEquals(
            calculateMonthTable(YearMonth.of(2023, 1), 1).toTypedArray(),
            Array(7 * 5) {
                LocalDate.of(2023, 1, 30).plusDays(it.toLong())
            }
        )
    }

    @Test
    fun `when calculateMonthTable with negative offset is called, should get range 2023-1-30 to 2023-3-5`() {
        assertArrayEquals(
            calculateMonthTable(YearMonth.of(2023, 1), -1).toTypedArray(),
            Array(7 * 5) {
                LocalDate.of(2022, 11, 28).plusDays(it.toLong())
            }
        )
    }
}