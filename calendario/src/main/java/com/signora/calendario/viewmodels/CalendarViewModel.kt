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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

class CalendarViewModel : ViewModel() {
    internal val cachedDates = LruCache<YearMonth, List<LocalDate>>(12 * 3)

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
            return visibleDates[visibleDates.size / 2].let { middleMonth ->
                when (expanded) {
                    true -> middleMonth[middleMonth.size / 2]
                    false -> currentWeek.let { (start, end) ->
                        if (middleMonth.count { it.monthValue == start.monthValue } > 3) start else end
                    }
                }.getYearMonth()
            }
        }

    init {
        formatDateCacheByRange(
            yearMonth = currentMonth,
            range = 3
        )
    }

    fun onIntent(intent: CalendarIntent) {
        when (intent) {
            is LoadDate -> {
                when (intent.period) {
                    CalendarPeriod.MONTH -> {
                        viewModelScope.launch {
                            val targetYearMonth = intent.date as YearMonth
                            currentMonth = targetYearMonth
                            currentWeek = targetYearMonth.atDay(1).getWeekRange()
                            visibleDates = formatMonthCalendarDate(
                                yearMonth = targetYearMonth,
                                range = intent.range.toLong()
                            )
                        }
                    }
                    CalendarPeriod.WEEK -> {
                        viewModelScope.launch {
                            val (targetHead, targetTail) = intent.date as Pair<*, *>
                            targetHead as LocalDate; targetTail as LocalDate
                            currentMonth = targetTail.getYearMonth()
                            currentWeek = targetHead to targetTail
                            visibleDates = formatWeekCalendarDate(
                                week = targetHead to targetTail,
                                range = intent.range.toLong()
                            )
                            if (targetHead.monthValue != targetTail.monthValue)
                                formatDateCacheByRange(
                                    yearMonth = targetTail.getYearMonth(),
                                    range = intent.range.toLong(),
                                    scope = this
                                )
                        }
                    }
                }
            }
            is SelectDate -> selectedDate = intent.date
            ExpandCalendar -> {
                viewModelScope.launch {
                    visibleDates = formatMonthCalendarDate(currentMonth)
                }
                expanded = true
            }
            CollapseCalendar -> {
                viewModelScope.launch {
                    (currentDay to selectedDate).let { (current, selected) ->
                        if (current.year == selected.year && current.monthValue == selected.monthValue &&
                            currentDay.until(selectedDate).days <= 14) current else selected
                    }.getWeekRange().let { (head, tail) ->
                        when {
                            head.year == currentMonth.year && head.monthValue == currentMonth.monthValue -> { // headTouch
                                if (head.monthValue != tail.monthValue)
                                    currentMonth = tail.getYearMonth()
                                currentWeek = head to tail
                            }
                            tail.year == currentMonth.year && tail.monthValue == currentMonth.monthValue -> { // tailTouch
                                currentWeek = head to tail
                            }
                        }
                    }
                    visibleDates = formatWeekCalendarDate(currentWeek)
                }
                expanded = false
            }
        }
    }

    internal fun formatWeekCalendarDate(week: Pair<LocalDate, LocalDate>, range: Long = 1): Array<List<LocalDate>> {
        return week.formatNeighborWeek(range).map { (head, tail) ->
            cachedDates[tail.getYearMonth()]?.run {
                subList(indexOf(head), indexOf(tail) + 1)
            } ?: head.getNextDates(7)
        }.toTypedArray()
    }

    internal fun formatMonthCalendarDate(yearMonth: YearMonth, range: Long = 1): Array<List<LocalDate>> {
        return yearMonth.formatNeighborMonth(range).map {
            cachedDates[it] ?: run {
                val table = calculateMonthTable(it)
                cachedDates.put(it, table)
                table
            }
        }.toTypedArray()
    }


    internal fun formatDateCacheByRange(yearMonth: YearMonth, range: Long = 1, scope: CoroutineScope = viewModelScope) {
        scope.launch {
            cachedDates[yearMonth] ?: run {
                cachedDates.put(yearMonth, calculateMonthTable(yearMonth))
            }

            (-range..range).forEach { offset ->
                launch {
                    val key = YearMonth.of(yearMonth.year, yearMonth.month).run {
                        when {
                            offset > 0 -> plusMonths(offset)
                            offset < 0 -> minusMonths(abs(offset))
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