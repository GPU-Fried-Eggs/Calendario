package com.signora.calendario.viewmodels

import com.signora.calendario.MainDispatcherRule
import com.signora.calendario.utils.calculateMonthTable
import com.signora.calendario.utils.getMonthStartDate
import com.signora.calendario.utils.getWeekStartDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random
import kotlin.random.nextInt

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    private lateinit var viewModel: CalendarViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() { viewModel = CalendarViewModel() }

    @Test
    fun `when use YearMonth as a Key of map collection, should get data by different instance`() {
        val storage = mapOf(
            YearMonth.now().minusMonths(1) to calculateMonthTable(LocalDate.now().getMonthStartDate(), -1),
            YearMonth.now() to calculateMonthTable(LocalDate.now().getMonthStartDate()),
            YearMonth.now().plusMonths(1) to calculateMonthTable(LocalDate.now().getMonthStartDate(), 1)
        )

        assert(storage[YearMonth.now().minusMonths(1)] is List)
        assert(storage[YearMonth.now()] is List)
        assert(storage[YearMonth.now().plusMonths(1)] is List)

        assert(storage[YearMonth.of(2000, 1)] == null)
    }

    @Test
    fun `sort YearMonth should be by correct order and linked hash map should keep the order`() {
        val yearMonthList = listOf(
            *generateSequence { Random.nextInt(1..20) }.distinct().take(10)
                .map { YearMonth.now().plusMonths(it.toLong()) }.toList().toTypedArray()
        )
        val sortedMap: MutableMap<YearMonth, String> = LinkedHashMap()
        yearMonthList.sorted().forEach { sortedMap[it] = it.toString() }

        var lastYearMonth = YearMonth.now()

        val result = sortedMap.keys.map {
            val correct = lastYearMonth.isBefore(it)
            lastYearMonth = it
            correct
        }

        assertArrayEquals(Array(10) { true }, result.toTypedArray())
    }

    @Test
    fun `all members in the view-model should be initialized`() {
        assertEquals(viewModel.dates.size, 3)
        assertEquals(viewModel.selectedDate, LocalDate.now())
        assertEquals(viewModel.currentWeek, LocalDate.now().getWeekStartDate())
        assertEquals(viewModel.currentMonth, YearMonth.now())
        assertEquals(viewModel.expanded, false)
    }

    @Test
    fun `when onIntent, should update state by CalendarIntent`() {

    }

    @Test
    fun `when loadDates is called, should load 1 + 2 + 2 dates`() {
        runTest {
            viewModel.formatDateByRange(YearMonth.of(2022, 6))
            advanceUntilIdle()
            assert(YearMonth.of(2022, 6) in viewModel.dates.keys)
            assert(YearMonth.of(2022, 6).plusMonths(1) in viewModel.dates.keys)
            assert(YearMonth.of(2022, 6).minusMonths(1) in viewModel.dates.keys)
        }
    }

    @Test
    fun `when moveForward is called, should move one month ahead`() {

    }

    //@Test
    //fun `when moveBackward is called, should move one month behind`() {
    //    viewModel.moveBackward(YearMonth.now().minusMonths(1))
    //    assert(1 is Number)
    //}
}