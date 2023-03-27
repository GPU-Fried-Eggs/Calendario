package com.signora.calendario

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.signora.calendario.ui.CalendarHeader
import com.signora.calendario.ui.theme.CalendarioTheme
import com.signora.calendario.ui.theme.CalenderTheme
import com.signora.calendario.ui.theme.darkColors
import com.signora.calendario.ui.theme.lightColors
import com.signora.calendario.viewmodels.CalendarViewModel
import java.time.LocalDate

@Composable
fun Calendar(
    darkTheme: Boolean = isSystemInDarkTheme(),
    onDateSelect: (LocalDate) -> Unit,
    calendarViewModel: CalendarViewModel = viewModel()
) {
    val colors = if (darkTheme) darkColors() else lightColors()

    CalenderTheme(
        colors = colors
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .background(CalendarioTheme.colors.backgroundColor)
        ) {
            CalendarHeader(
                currentMonth = calendarViewModel.currentMonth,
                expanded = calendarViewModel.expanded,
                onStateChange = {

                }
            )
        }
    }
}