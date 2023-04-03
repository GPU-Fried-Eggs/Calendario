package com.signora.samples.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.signora.calendario.ExpandableCalendar
import com.signora.calendario.ui.theme.CalendarTheme
import java.time.LocalDate

@Composable
fun Calendar() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    CalendarTheme {
        Column {
            ExpandableCalendar(
                onDateSelect = {
                    currentDate = it
                }
            )
            Text(
                text = "Selected date: $currentDate",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun CalendarPreview() {
    Calendar()
}