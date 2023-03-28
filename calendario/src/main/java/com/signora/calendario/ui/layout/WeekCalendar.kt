package com.signora.calendario.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.signora.calendario.models.CalendarIntent
import com.signora.calendario.models.CalendarItemState.Companion.getState
import com.signora.calendario.models.CalendarPeriod
import com.signora.calendario.ui.CalendarPager
import com.signora.calendario.ui.theme.CalendarioTheme
import com.signora.calendario.viewmodels.CalendarViewModel
import com.signora.calendario.views.CalenderItemView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun WeekCalendar(
    loadedDates: Array<List<LocalDate>>,
    loadedMonthWeek: Array<Pair<YearMonth, Pair<LocalDate, LocalDate>?>>,
    loadDatesForMonthWeek: (Pair<YearMonth, Pair<LocalDate, LocalDate>?>) -> Unit,
    selectedDate: LocalDate? = null,
    onDateSelect: (LocalDate) -> Unit,
    childrenHeaderContent: @Composable (() -> Unit)? = null,
    childrenFooterContent: @Composable (() -> Unit)? = null
) {
    CalendarPager(
        loadedMonthWeek = loadedMonthWeek,
        loadPrevDates = loadDatesForMonthWeek,
        loadNextDates = loadDatesForMonthWeek
    ) { currentPage ->
        BoxWithConstraints {
            val parentWidth = this.maxWidth

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                loadedDates[currentPage].forEach { date ->
                    Box(
                        modifier = Modifier
                            .width(parentWidth / 7f)
                            .padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CalenderItemView(
                            modifier = Modifier.alpha(if (date.isBefore(LocalDate.now())) 0.5f else 1f),
                            date = date,
                            state = getState(date, selectedDate),
                            onDateSelect = onDateSelect,
                            headerContent = {
                                childrenHeaderContent?.let { it() } ?: Text(
                                    DayOfWeek.values()[date.dayOfWeek.value - 1].getDisplayName(
                                        java.time.format.TextStyle.SHORT,
                                        LocalContext.current.resources.configuration.locales[0]
                                    ),
                                    color = CalendarioTheme.colors.onBackgroundColor,
                                    fontSize = 10.sp
                                )
                            },
                            footerContent = childrenFooterContent
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
    val calendarViewModel: CalendarViewModel = viewModel()

    WeekCalendar(
        loadedDates = calendarViewModel.visibleDates,
        loadedMonthWeek = calendarViewModel.formatNeighborWeek(calendarViewModel.currentWeek).toTypedArray(),
        loadDatesForMonthWeek = {
            calendarViewModel.onIntent(CalendarIntent.LoadDate(it, period = CalendarPeriod.WEEK))
        },
        selectedDate = calendarViewModel.selectedDate,
        onDateSelect = {}
    )
}