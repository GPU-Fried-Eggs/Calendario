package com.signora.calendario.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signora.calendario.R
import com.signora.calendario.models.CalendarIntent
import com.signora.calendario.models.CalendarIntent.CollapseCalendar
import com.signora.calendario.models.CalendarIntent.ExpandCalendar
import com.signora.calendario.ui.theme.CalendarTheme
import java.time.YearMonth
import java.time.format.TextStyle

@Composable
fun CalendarHeader(
    currentYearMonth: YearMonth,
    expanded: Boolean,
    onStateChange: (CalendarIntent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .background(CalendarTheme.colors.backgroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            text = "${
                currentYearMonth.month.getDisplayName(
                    TextStyle.FULL_STANDALONE,
                    LocalContext.current.resources.configuration.locales[0]
                )
            } ${currentYearMonth.year}",
            color = CalendarTheme.colors.onBackgroundColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.weight(1f))
        IconToggleButton(
            checked = expanded,
            onCheckedChange = { checked ->
                onStateChange(if (checked) ExpandCalendar else CollapseCalendar)
            }
        ) {
            Icon(
                imageVector = Icons.Default.run { if (expanded) KeyboardArrowUp else KeyboardArrowDown },
                contentDescription = stringResource(R.string.calendar_toggle_icon_description),
                tint = CalendarTheme.colors.onBackgroundColor
            )
        }
    }
}

@Preview
@Composable
private fun CalendarHeaderPreview() {
    var calendarExpanded by remember { mutableStateOf(false) }

    CalendarHeader(
        currentYearMonth = YearMonth.now(),
        expanded = calendarExpanded,
        onStateChange = { calendarExpanded = !calendarExpanded }
    )
}