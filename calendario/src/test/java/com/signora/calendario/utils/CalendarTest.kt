package com.signora.calendario.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class CalendarTest {
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

    @Test
    fun `when formatNeighborWeek is called, should return a group of YearMonth`() {
        assertArrayEquals(
            arrayOf(
                LocalDate.of(2022, 12, 19) to LocalDate.of(2022, 12, 25),
                LocalDate.of(2022, 12, 26) to LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 2) to LocalDate.of(2023, 1, 8)
            ),
            (LocalDate.of(2022, 12, 26) to LocalDate.of(2023, 1, 1)).formatNeighborWeek().toTypedArray()
        )
    }

    @Test
    fun `when formatNeighborWeek with 3 range is called, should return a group of YearMonth`() {
        assertArrayEquals(
            arrayOf(
                LocalDate.of(2022, 12, 5) to LocalDate.of(2022, 12, 11),
                LocalDate.of(2022, 12, 12) to LocalDate.of(2022, 12, 18),
                LocalDate.of(2022, 12, 19) to LocalDate.of(2022, 12, 25),
                LocalDate.of(2022, 12, 26) to LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 2) to LocalDate.of(2023, 1, 8),
                LocalDate.of(2023, 1, 9) to LocalDate.of(2023, 1, 15),
                LocalDate.of(2023, 1, 16) to LocalDate.of(2023, 1, 22)
            ),
            (LocalDate.of(2022, 12, 26) to LocalDate.of(2023, 1, 1)).formatNeighborWeek(3).toTypedArray()
        )
    }

    @Test
    fun `when formatNeighborMonth is called, should return a group of YearMonth`() {
        assertArrayEquals(
            arrayOf(
                YearMonth.of(2022, 12),
                YearMonth.of(2023, 1),
                YearMonth.of(2023, 2)
            ),
            YearMonth.of(2023, 1).formatNeighborMonth().toTypedArray()
        )
    }

    @Test
    fun `when formatNeighborMonth with 3 range is called, should return a group of YearMonth`() {
        assertArrayEquals(
            arrayOf(
                YearMonth.of(2022, 10),
                YearMonth.of(2022, 11),
                YearMonth.of(2022, 12),
                YearMonth.of(2023, 1),
                YearMonth.of(2023, 2),
                YearMonth.of(2023, 3),
                YearMonth.of(2023, 4)
            ),
            YearMonth.of(2023, 1).formatNeighborMonth(3).toTypedArray()
        )
    }
}