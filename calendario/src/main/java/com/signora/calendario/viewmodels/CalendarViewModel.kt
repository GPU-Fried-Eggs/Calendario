package com.signora.calendario.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signora.calendario.models.CalendarIntent
import com.signora.calendario.models.CalendarIntent.*
import com.signora.calendario.models.Period
import com.signora.calendario.models.Week
import com.signora.calendario.utils.calculateMonthTable
import com.signora.calendario.utils.getMonthStartDate
import com.signora.calendario.utils.getWeekStartDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

class CalendarViewModel : ViewModel() {
    /**
     * The dates in view model is a map with 3(types) × (7 × weeks). A week allocator (currentWeek)
     * could located the position of week calendar in the month calendar.
     */
    var dates = mutableStateMapOf(
        YearMonth.now().minusMonths(1) to calculateMonthTable(LocalDate.now().getMonthStartDate(), -1),
        YearMonth.now() to calculateMonthTable(LocalDate.now().getMonthStartDate()),
        YearMonth.now().plusMonths(1) to calculateMonthTable(LocalDate.now().getMonthStartDate(), 1)
    )

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    var currentMonth by mutableStateOf(YearMonth.now())
        private set

    var currentWeek by mutableStateOf(Week.now())
        private set

    var expanded by mutableStateOf(false)
        private set

    val visibleDates: Array<List<LocalDate>>
        get() {
            return when (expanded) {
                true -> Array(3) {
                    when {
                        it < 1 -> dates[YearMonth.of(currentMonth.year, currentMonth.month).minusMonths(1)]!!
                        it > 1 -> dates[YearMonth.of(currentMonth.year, currentMonth.month).plusMonths(1)]!!
                        else -> dates[currentMonth]!!
                    }
                }
                false -> Array(3) {
                    when {
                        it < 1 -> dates[YearMonth.of(currentMonth.year, currentMonth.month).minusMonths(1)]!!
                        it > 1 -> dates[YearMonth.of(currentMonth.year, currentMonth.month).plusMonths(1)]!!
                        else -> dates[currentMonth]!!
                    }
                }
            }
        }

    fun onIntent(intent: CalendarIntent) {
        when (intent) {
            is LoadNextDate -> {
                when (intent.period) {
                    Period.MONTH -> {
                        formatDateByRange(intent.yearMonth, intent.range)
                        currentMonth = intent.yearMonth
                        currentWeek = Week.of(intent.yearMonth)
                    }
                    Period.WEEK -> {

                    }
                }
            }
            is SelectDate -> {
                selectedDate = intent.date
            }
            ExpandCalendar -> {
                // should move current month focus
                expanded = true
            }
            CollapseCalendar -> {
                // should move current week focus
                expanded = false
            }
        }
    }

    internal fun formatDateByRange(yearMonth: YearMonth, range: Int = 1) {
        viewModelScope.launch {
            if (yearMonth !in dates) dates[yearMonth] = calculateMonthTable(yearMonth.atDay(1))

            (-range..range).forEach {
                launch {
                    val key = YearMonth.of(yearMonth.year, yearMonth.month).run {
                        when {
                            it > 0 -> plusMonths(it.toLong())
                            it < 0 -> minusMonths(abs(it.toLong()))
                            else -> null
                        }
                    }
                    if (key != null && key !in dates)
                        dates[key] = calculateMonthTable(yearMonth.atDay(1), it)
                }
            }
        }
    }
}