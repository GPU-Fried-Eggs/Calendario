package com.signora.calendario.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signora.calendario.models.CalenderItemState.Companion.getState
import com.signora.calendario.ui.CalendarPager
import com.signora.calendario.views.CalenderItemView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@Composable
fun MonthCalendar(
    loadedDates: List<List<LocalDate>>,
    currentMonth: YearMonth,
    loadDatesForMonth: (YearMonth) -> Unit,
    selectedDate: LocalDate? = null,
    onDateSelect: (LocalDate) -> Unit
) {
    CalendarPager(
        loadedDates = loadedDates,
        loadNextDates = { loadDatesForMonth(currentMonth) },
        loadPrevDates = { loadDatesForMonth(currentMonth.minusMonths(2)) }
    ) { currentPage ->
        BoxWithConstraints {
            val boxWithConstraintsScope = this

            Row(Modifier.height(355.dp)) {
                loadedDates[currentPage].forEachIndexed { index, date ->
                    Box(
                        modifier = Modifier
                            .width(boxWithConstraintsScope.maxWidth)
                            .padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CalenderItemView(
                            modifier = Modifier.alpha(
                                if (date.isBefore(LocalDate.now()) ||
                                    date.isAfter(currentMonth.atEndOfMonth()) ||
                                    date.isBefore(currentMonth.atDay(1)))
                                    0.5f else 1f
                            ),
                            date = date,
                            state = getState(date, selectedDate),
                            onDateSelect = { onDateSelect(date) },
                            headerContent = {
                                if (index < 7)
                                    Text(
                                        DayOfWeek.values()[date.dayOfWeek.value - 1].getDisplayName(
                                            TextStyle.SHORT,
                                            LocalContext.current.resources.configuration.locales[0]
                                        ),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                            }

                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MonthCalendarPreview() {
    MonthCalendar(
        loadedDates = listOf(),
        currentMonth = YearMonth.now(),
        loadDatesForMonth = {},
        selectedDate = null,
        onDateSelect = {}
    )
}