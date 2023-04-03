package com.signora.calendario.viewmodels

import com.signora.calendario.MainDispatcherRule
import com.signora.calendario.utils.calculateMonthTable
import com.signora.calendario.utils.getNextDates
import com.signora.calendario.utils.getWeekRange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    private lateinit var viewModel: CalendarViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() { viewModel = CalendarViewModel() }

    @Test
    fun `all members in the view-model should be initialized`() {
        assertEquals(viewModel.visibleDates.size, 3)
        assertEquals(viewModel.selectedDate, LocalDate.now())
        assertEquals(viewModel.currentWeek, LocalDate.now().getWeekRange())
        assertEquals(viewModel.currentMonth, YearMonth.now())
        assertEquals(viewModel.expanded, false)
    }

    @Test
    fun `when onIntent, should update state by CalendarIntent`() {

    }

    @Test
    fun `when formatWeekCalendarDate is called, should load or get from cache`() {
        assertArrayEquals(
            LocalDate.of(2022, 12, 26).minusWeeks(1).getNextDates(21).chunked(7).toTypedArray(),
            viewModel.formatWeekCalendarDate(LocalDate.of(2022, 12, 26) to LocalDate.of(2023, 1, 1))
        )
    }

    @Test
    fun `when formatMonthCalendarDate is called, should load or get from cache`() {
        assertArrayEquals(
            arrayOf(
                calculateMonthTable(YearMonth.of(2022, 12)),
                calculateMonthTable(YearMonth.of(2023, 1)),
                calculateMonthTable(YearMonth.of(2023, 2))
            ),
            viewModel.formatMonthCalendarDate(YearMonth.of(2023, 1))
        )
    }

    @Test
    fun `when formatDateByRange is called, should load 1 + 1 + 1 dates`() {
        runTest {
            viewModel.formatDateCacheByRange(YearMonth.of(2022, 6))
            advanceUntilIdle()
            assert(YearMonth.of(2022, 5) in viewModel.cachedDates.snapshot())
            assert(YearMonth.of(2022, 6) in viewModel.cachedDates.snapshot())
            assert(YearMonth.of(2022, 7) in viewModel.cachedDates.snapshot())
        }
    }

    @Test
    fun `when formatDateByRange with range 2 is called, should load 1 + 2 + 2 dates`() {
        runTest {
            viewModel.formatDateCacheByRange(YearMonth.of(2022, 6), 2)
            advanceUntilIdle()
            assert(YearMonth.of(2022, 4) in viewModel.cachedDates.snapshot())
            assert(YearMonth.of(2022, 5) in viewModel.cachedDates.snapshot())
            assert(YearMonth.of(2022, 6) in viewModel.cachedDates.snapshot())
            assert(YearMonth.of(2022, 7) in viewModel.cachedDates.snapshot())
            assert(YearMonth.of(2022, 8) in viewModel.cachedDates.snapshot())
        }
    }
}