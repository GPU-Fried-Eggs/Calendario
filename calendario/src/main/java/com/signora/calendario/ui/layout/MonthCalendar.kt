package com.signora.calendario.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.signora.calendario.ui.CalendarPager
import com.signora.calendario.ui.theme.CalendarioTheme
import com.signora.calendario.viewmodels.CalendarViewModel
import com.signora.calendario.views.CalenderItemView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthCalendar(
    loadedDates: Array<List<LocalDate>>,
    loadedMonth: Array<YearMonth>,
    loadDatesForMonth: (Pair<YearMonth, Pair<LocalDate, LocalDate>?>) -> Unit,
    selectedDate: LocalDate? = null,
    onDateSelect: (LocalDate) -> Unit,
    childrenHeaderContent: @Composable (() -> Unit)? = null,
    childrenFooterContent: @Composable (() -> Unit)? = null
) {
    CalendarPager(
        loadedMonthWeek = loadedMonth.map { Pair(it, null) }.toTypedArray(),
        loadNextDates = loadDatesForMonth,
        loadPrevDates = loadDatesForMonth
    ) { currentPage ->
        BoxWithConstraints {
            val parentWidth = this.maxWidth

            FlowRow(Modifier.height(355.dp)) {
                loadedDates[currentPage].forEachIndexed { index, date ->
                    Box(
                        modifier = Modifier
                            .width(parentWidth / 7f)
                            .padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CalenderItemView(
                            modifier = Modifier.alpha(
                                if (date.isAfter(loadedMonth[currentPage].atEndOfMonth()) ||
                                    date.isBefore(loadedMonth[currentPage].atDay(1)))
                                    0.5f else 1f
                            ),
                            date = date,
                            state = getState(date, selectedDate),
                            onDateSelect = { onDateSelect(date) },
                            headerContent = {
                                childrenHeaderContent?.let { it() } ?: (
                                    if (index < 7)
                                        Text(
                                            DayOfWeek.values()[date.dayOfWeek.value - 1].getDisplayName(
                                                java.time.format.TextStyle.SHORT,
                                                LocalContext.current.resources.configuration.locales[0]
                                            ),
                                            color = CalendarioTheme.colors.onBackgroundColor,
                                            fontSize = 10.sp
                                        )
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
private fun MonthCalendarPreview() {
    val calendarViewModel: CalendarViewModel = viewModel()

    LaunchedEffect(Unit) {
        calendarViewModel.onIntent(CalendarIntent.ExpandCalendar)
    }

    MonthCalendar(
        loadedDates = calendarViewModel.visibleDates,
        loadedMonth = arrayOf(
            calendarViewModel.currentMonth.minusMonths(1),
            calendarViewModel.currentMonth,
            calendarViewModel.currentMonth.plusMonths(1)
        ),
        loadDatesForMonth = {
            calendarViewModel.onIntent(CalendarIntent.LoadDate(it))
        },
        selectedDate = calendarViewModel.selectedDate,
        onDateSelect = {}
    )
}