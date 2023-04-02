package com.signora.calendario.viewmodels

import androidx.collection.LruCache
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signora.calendario.models.CalendarIntent
import com.signora.calendario.models.CalendarIntent.*
import com.signora.calendario.models.CalendarPeriod
import com.signora.calendario.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

class CalendarViewModel : ViewModel() {
    val cachedDates = LruCache<YearMonth, List<LocalDate>>(12 * 3).apply {
        put(YearMonth.now().minusMonths(1), calculateMonthTable(YearMonth.now(), -1))
        put(YearMonth.now(), calculateMonthTable(YearMonth.now()))
        put(YearMonth.now().plusMonths(1), calculateMonthTable(YearMonth.now(), 1))
    }

    var visibleDates by mutableStateOf(formatWeekCalendarDate(LocalDate.now().getWeekRange()))
        private set

    var currentDay by mutableStateOf(LocalDate.now())
        private set

    var currentWeek by mutableStateOf(LocalDate.now().getWeekRange())
        private set

    var currentMonth by mutableStateOf(LocalDate.now().getWeekEndDate().getYearMonth())
        private set

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    var expanded by mutableStateOf(false)
        private set

    val displayMonth: YearMonth
        get() {
            val current = visibleDates[1]
            return when (expanded) {
                true -> current[current.size / 2]
                false -> currentWeek.let { (start, end) ->
                    if (current.count { it.monthValue == start.monthValue } > 3) start else end
                }
            }.getYearMonth()
        }

    init { formatDateCacheByRange(currentMonth, 6) }

    fun onIntent(intent: CalendarIntent) {
        when (intent) {
            is LoadDate -> {
                when (intent.period) {
                    CalendarPeriod.MONTH -> {
                        currentMonth = intent.monthWeek.first
                        currentWeek = intent.monthWeek.first.atDay(1).getWeekRange()
                        visibleDates = formatMonthCalendarDate(currentMonth)
                        formatDateCacheByRange(currentMonth, intent.range)
                    }
                    CalendarPeriod.WEEK -> {
                        currentMonth = intent.monthWeek.first
                        currentWeek = requireNotNull(intent.monthWeek.second)
                        visibleDates = formatWeekCalendarDate(currentWeek)
                        if (currentWeek.first.monthValue != currentWeek.second.monthValue)
                            formatDateCacheByRange(currentMonth, intent.range)
                    }
                }
            }
            is SelectDate -> selectedDate = intent.date
            ExpandCalendar -> {
                viewModelScope.launch(Dispatchers.IO) {
                    visibleDates = formatMonthCalendarDate(currentMonth)
                }
                expanded = true
            }
            CollapseCalendar -> {
                viewModelScope.launch(Dispatchers.IO) {
                    Pair(currentDay, selectedDate).let {
                        if (it.first.monthValue == it.second.monthValue && it.first.year == it.second.year &&
                            currentDay.until(selectedDate).days <= 14) it.first else it.second
                    }.getWeekRange().let {
                        if (it.first.monthValue != it.second.monthValue)
                            if (it.second.monthValue != currentMonth.monthValue ||
                                it.second.year != currentMonth.year)
                                currentMonth = it.second.getYearMonth()
                        if (it.first != currentWeek.first || it.second != currentWeek.second)
                            currentWeek = it
                    }
                    visibleDates = formatWeekCalendarDate(currentWeek)
                }
                expanded = false
            }
        }
    }

    internal fun formatNeighborWeek(week: Pair<LocalDate, LocalDate>, rangeOfWeek: Long = 1): List<Pair<YearMonth, Pair<LocalDate, LocalDate>>> {
        val (lastStartDay, lastEndDay) = Pair(week.first.plusWeeks(rangeOfWeek), week.second.plusWeeks(rangeOfWeek))
        return List((1 + (2 * rangeOfWeek)).toInt()) {
            val current = Pair(lastStartDay.minusWeeks(it.toLong()), lastEndDay.minusWeeks(it.toLong()))
            Pair(current.second.getYearMonth(), current)
        }.reversed()
    }

    internal fun formatWeekCalendarDate(week: Pair<LocalDate, LocalDate>): Array<List<LocalDate>> {
        return formatNeighborWeek(week).map { (yearMonth, week) ->
            cachedDates[yearMonth]?.let {
                it.subList(it.indexOf(week.first), it.indexOf(week.second) + 1)
            } ?: week.first.getNextDates(7)
        }.toTypedArray()
    }

    internal fun formatMonthCalendarDate(yearMonth: YearMonth): Array<List<LocalDate>> {
        return Array(3) { index ->
            when {
                index < 1 -> {
                    val prevYearMonth = yearMonth.minusMonths(1)
                    cachedDates[prevYearMonth] ?: run {
                        val table = calculateMonthTable(prevYearMonth)
                        cachedDates.put(prevYearMonth, table)
                        table
                    }
                }
                index > 1 -> {
                    val nextYearMonth = yearMonth.plusMonths(1)
                    cachedDates[nextYearMonth] ?: run {
                        val table = calculateMonthTable(nextYearMonth)
                        cachedDates.put(nextYearMonth, table)
                        table
                    }
                }
                else -> {
                    cachedDates[yearMonth] ?: run {
                        val table = calculateMonthTable(yearMonth)
                        cachedDates.put(yearMonth, table)
                        table
                    }
                }
            }
        }
    }

    internal fun formatDateCacheByRange(yearMonth: YearMonth, range: Int = 1) {
        viewModelScope.launch {
            cachedDates[yearMonth] ?: run {
                cachedDates.put(yearMonth, calculateMonthTable(yearMonth))
            }

            (-range..range).forEach { offset ->
                launch {
                    val key = YearMonth.of(yearMonth.year, yearMonth.month).run {
                        when {
                            offset > 0 -> plusMonths(offset.toLong())
                            offset < 0 -> minusMonths(abs(offset.toLong()))
                            else -> null
                        }
                    }
                    key?.let { cachedDates[key] ?: run {
                        cachedDates.put(key, calculateMonthTable(yearMonth, offset))
                    }}
                }
            }
        }
    }
}