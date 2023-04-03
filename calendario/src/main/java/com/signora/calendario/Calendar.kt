package com.signora.calendario

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.signora.calendario.models.CalendarIntent
import com.signora.calendario.models.CalendarPeriod
import com.signora.calendario.ui.CalendarHeader
import com.signora.calendario.ui.layout.MonthCalendar
import com.signora.calendario.ui.layout.WeekCalendar
import com.signora.calendario.ui.theme.*
import com.signora.calendario.utils.formatNeighborMonth
import com.signora.calendario.utils.formatNeighborWeek
import com.signora.calendario.viewmodels.CalendarViewModel
import java.time.LocalDate

@Composable
fun Calendar(
    onDateSelect: (LocalDate) -> Unit = {},
    calendarViewModel: CalendarViewModel = viewModel(),
    headerContent: @Composable ((CalendarViewModel) -> Unit)? = null,
    footerContent: @Composable ((CalendarViewModel) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .background(CalendarTheme.colors.backgroundColor)
    ) {
        headerContent?.let { it(calendarViewModel) }
        if (calendarViewModel.expanded) {
            MonthCalendar(
                loadedDates = calendarViewModel.visibleDates,
                loadedYearMonth = calendarViewModel.currentMonth.formatNeighborMonth().toTypedArray(),
                loadDatesForYearMonth = {
                    calendarViewModel.onIntent(CalendarIntent.LoadDate(it))
                },
                selectedDate = calendarViewModel.selectedDate,
                onDateSelect = {
                    calendarViewModel.onIntent(CalendarIntent.SelectDate(it))
                    onDateSelect(it)
                }
            )
        } else {
            WeekCalendar(
                loadedDates = calendarViewModel.visibleDates,
                loadedMonthWeek = calendarViewModel.currentWeek.formatNeighborWeek().toTypedArray(),
                loadDatesForMonthWeek = {
                    calendarViewModel.onIntent(CalendarIntent.LoadDate(it, period = CalendarPeriod.WEEK))
                },
                selectedDate = calendarViewModel.selectedDate,
                onDateSelect = {
                    calendarViewModel.onIntent(CalendarIntent.SelectDate(it))
                    onDateSelect(it)
                }
            )
        }
        footerContent?.let { it(calendarViewModel) }
    }
}

@Composable
fun ExpandableCalendar(
    onDateSelect: (LocalDate) -> Unit = {},
    headerContent: @Composable ((CalendarViewModel) -> Unit)? = null,
    footerContent: @Composable ((CalendarViewModel) -> Unit)? = null
) {
    Calendar(
        onDateSelect = onDateSelect,
        headerContent = headerContent ?: { viewModel ->
            CalendarHeader(
                currentYearMonth = viewModel.displayMonth,
                expanded = viewModel.expanded,
                onStateChange = { viewModel.onIntent(it) }
            )
        },
        footerContent = footerContent
    )
}

@Composable
fun KanbanCalendar(
    onDateSelect: (LocalDate) -> Unit = {},
    headerContent: @Composable ((CalendarViewModel) -> Unit)? = null,
    footerContent: @Composable ((CalendarViewModel) -> Unit)? = null
) {
    Calendar(
        onDateSelect = onDateSelect,
        headerContent = headerContent ?: { viewModel ->
            CalendarHeader(
                currentYearMonth = viewModel.displayMonth,
                expanded = viewModel.expanded,
                onStateChange = { viewModel.onIntent(it) }
            )
        },
        footerContent = footerContent
    )
}

@Preview
@Composable
private fun CalendarPreview() {
    Calendar()
}

@Preview
@Composable
private fun ExpandableCalendarPreview() {
    ExpandableCalendar()
}

@Preview
@Composable
private fun KanbanCalendarPreview() {
    KanbanCalendar()
}