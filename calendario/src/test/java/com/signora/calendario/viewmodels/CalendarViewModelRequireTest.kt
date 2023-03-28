package com.signora.calendario.viewmodels

import com.signora.calendario.utils.calculateMonthTable
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random
import kotlin.random.nextInt

class CalendarViewModelRequireTest {
    @Test
    fun `when use YearMonth as a Key of map collection, should get data by different instance`() {
        val storage = mapOf(
            YearMonth.now().minusMonths(1) to calculateMonthTable(YearMonth.now(), -1),
            YearMonth.now() to calculateMonthTable(YearMonth.now()),
            YearMonth.now().plusMonths(1) to calculateMonthTable(YearMonth.now(), 1)
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

        Assert.assertArrayEquals(Array(10) { true }, result.toTypedArray())
    }

    @Test
    fun `find local date from list`() {
        val collection = listOf(
            LocalDate.of(2022, 10, 1),
            LocalDate.of(2022, 10, 2),
            LocalDate.of(2022, 10, 3)
        )

        assert(LocalDate.of(2022, 10, 1) in collection)
        assert(LocalDate.of(2022, 10, 2) in collection)
        assert(LocalDate.of(2022, 10, 3) in collection)

        Assert.assertEquals(collection.indexOf(LocalDate.of(2022, 10, 1)), 0)
        Assert.assertEquals(collection.indexOf(LocalDate.of(2022, 10, 2)), 1)
        Assert.assertEquals(collection.indexOf(LocalDate.of(2022, 10, 3)), 2)
    }
}