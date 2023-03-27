package com.signora.calendario.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.models.CalenderItemState.Companion.getState
import com.signora.calendario.ui.CalendarPager
import com.signora.calendario.views.CalenderItemView
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeekCalendar(
    loadedDates: List<List<LocalDate>>,
    loadNextDates: (LocalDate) -> Unit,
    loadPrevDates: (LocalDate) -> Unit,
    selectedDate: LocalDate? = null,
    onDateSelect: (LocalDate) -> Unit
) {
    CalendarPager(
        loadedDates = loadedDates,
        loadNextDates = loadNextDates,
        loadPrevDates = loadPrevDates
    ) { currentPage ->
        BoxWithConstraints {
            val boxWithConstraintsScope = this

            Row {
                loadedDates[currentPage].forEach { date ->
                    Box(
                        modifier = Modifier
                            .width(boxWithConstraintsScope.maxWidth / 7f)
                            .padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CalenderItemView(
                            modifier = Modifier.alpha(if (date.isBefore(LocalDate.now())) 0.5f else 1f),
                            date = date,
                            state = getState(date, selectedDate),
                            onDateSelect = onDateSelect
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WeekCalendarPreview() {
    val dates = List(7 * 3) {
        LocalDate.now().run {
            var date = this
            while (date.dayOfWeek != DayOfWeek.MONDAY) {
                date = date.minusDays(1)
            }
            date
        }.minusWeeks(1).plusDays(it.toLong())
    }

    Column {
        WeekCalendar(
            loadedDates = List(3) {
                dates.slice(it * 7 until (it + 1) * 7)
            },
            loadNextDates = {},
            loadPrevDates = {},
            selectedDate = LocalDate.now().plusDays(2),
            onDateSelect = {}
        )
        WeekCalendar(
            loadedDates = List(3) {
                dates.slice(it * 7 until (it + 1) * 7)
            },
            loadNextDates = {},
            loadPrevDates = {},
            selectedDate = null,
            onDateSelect = {}
        )
    }
}