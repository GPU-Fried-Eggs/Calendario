package com.signora.calendario.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class LocalDateTest {
    @Test
    fun `when getNextDates is called, should return a group of date after current date`() {
        assertArrayEquals(
            Array(3) {
                LocalDate.of(2023, 1, 1).plusDays(it.toLong())
            },
            LocalDate.of(2023, 1, 1).getNextDates(3).toTypedArray()
        )
    }

    @Test
    fun `when getWeekStartDate is called, should get the beginning date of a week by current date`() {
        assertEquals(
            LocalDate.of(2022, 12, 26),
            LocalDate.of(2023, 1, 1).getWeekStartDate()
        )
    }

    @Test
    fun `when getRemainingDatesInWeek is called, should get the date collection of a week by current date`() {
        assertArrayEquals(
            Array(5) {
                LocalDate.of(2023, 2, 1).plusDays(it.toLong())
            },
            LocalDate.of(2023, 1, 31).getRemainingDatesInWeek().toTypedArray()
        )
    }

    @Test
    fun `when getMonthStartDate is called, should get the beginning date of a month base on Monday by current date`() {
        assertEquals(
            LocalDate.of(2022, 12, 26),
            LocalDate.of(2023, 1, 1).getWeekStartDate()
        )
    }

    @Test
    fun `when getRemainingDatesInMonth is called, should get the date collection of a month by current date`() {
        assertArrayEquals(
            Array(2) {
                LocalDate.of(2023, 1, 30).plusDays(it.toLong())
            },
            LocalDate.of(2023, 1, 30).getRemainingDatesInMonth().toTypedArray()
        )
    }
}