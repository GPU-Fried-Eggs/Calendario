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
        assertEquals(viewModel.cachedDates.size(), 3)
        assert(YearMonth.now().minusMonths(1) in viewModel.cachedDates.snapshot())
        assert(YearMonth.now() in viewModel.cachedDates.snapshot())
        assert(YearMonth.now().plusMonths(1) in viewModel.cachedDates.snapshot())
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
    fun `when formatNeighborWeek is called, should return a group of YearMonth`() {
        assertArrayEquals(
            arrayOf(
                Pair(YearMonth.of(2022, 12), Pair(LocalDate.of(2022, 12, 19), LocalDate.of(2022, 12, 25))),
                Pair(YearMonth.of(2023, 1), Pair(LocalDate.of(2022, 12, 26), LocalDate.of(2023, 1, 1))),
                Pair(YearMonth.of(2023, 1), Pair(LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 8)))
            ),
            viewModel.formatNeighborWeek(Pair(LocalDate.of(2022, 12, 26), LocalDate.of(2023, 1, 1))).toTypedArray()
        )
    }

    @Test
    fun `when formatNeighborWeek with 3 range is called, should return a group of YearMonth`() {
        assertArrayEquals(
            arrayOf(
                Pair(YearMonth.of(2022, 12), Pair(LocalDate.of(2022, 12, 5), LocalDate.of(2022, 12, 11))),
                Pair(YearMonth.of(2022, 12), Pair(LocalDate.of(2022, 12, 12), LocalDate.of(2022, 12, 18))),
                Pair(YearMonth.of(2022, 12), Pair(LocalDate.of(2022, 12, 19), LocalDate.of(2022, 12, 25))),
                Pair(YearMonth.of(2023, 1), Pair(LocalDate.of(2022, 12, 26), LocalDate.of(2023, 1, 1))),
                Pair(YearMonth.of(2023, 1), Pair(LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 8))),
                Pair(YearMonth.of(2023, 1), Pair(LocalDate.of(2023, 1, 9), LocalDate.of(2023, 1, 15))),
                Pair(YearMonth.of(2023, 1), Pair(LocalDate.of(2023, 1, 16), LocalDate.of(2023, 1, 22)))
            ),
            viewModel.formatNeighborWeek(Pair(LocalDate.of(2022, 12, 26), LocalDate.of(2023, 1, 1)), 3).toTypedArray()
        )
    }

    @Test
    fun `when formatWeekCalendarDate is called should load or get from cache`() {
        assertArrayEquals(
            LocalDate.of(2022, 12, 26).minusWeeks(1).getNextDates(21).chunked(7).toTypedArray(),
            viewModel.formatWeekCalendarDate(Pair(LocalDate.of(2022, 12, 26), LocalDate.of(2023, 1, 1)))
        )
    }

    @Test
    fun `when formatMonthCalendarDate is called should load or get from cache`() {
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