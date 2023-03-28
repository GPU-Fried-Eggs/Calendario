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
    fun `when getWeekEndDate is called, should get the end date of a week by current date`() {
        assertEquals(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 1).getWeekEndDate()
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
    fun `when getWeekRange is called, should get a pair of start day and end day`() {
        assertEquals(
            Pair(
                LocalDate.of(2022, 12, 26),
                LocalDate.of(2023, 1, 1)
            ),
            LocalDate.of(2023, 1, 1).getWeekRange()
        )
    }

    @Test
    fun `when getMonthStartDate is called, should get the beginning date of a month by current date`() {
        assertEquals(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 1).getMonthStartDate()
        )
    }

    @Test
    fun `when getMonthEndDate is called, should get the end date of a month by current date`() {
        assertEquals(
            LocalDate.of(2023, 1, 31),
            LocalDate.of(2023, 1, 1).getMonthEndDate()
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