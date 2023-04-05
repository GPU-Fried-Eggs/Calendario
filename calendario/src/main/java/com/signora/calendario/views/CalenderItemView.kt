package com.signora.calendario.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signora.calendario.models.CalendarItemState
import com.signora.calendario.models.CalendarItemState.*
import com.signora.calendario.ui.theme.CalendarTheme
import java.time.LocalDate

@Composable
internal fun CalenderItemView(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    state: CalendarItemState = Default,
    date: LocalDate,
    onDateSelect: (LocalDate) -> Unit,
    headerContent: @Composable (() -> Unit)? = null,
    footerContent: @Composable (() -> Unit)? = null
) {
    val containerModifier = modifier.run {
        when (state) {
            is Selected -> background(
                color = CalendarTheme.colors.selectedItemBackgroundColor,
                shape = CalendarTheme.shapes.itemShape
            )
            is Today -> background(
                color = CalendarTheme.colors.todayItemBackgroundColor,
                shape = CalendarTheme.shapes.itemShape
            )
            is Default -> background(
                color = CalendarTheme.colors.defaultItemBackgroundColor,
                shape = CalendarTheme.shapes.itemShape
            )
        }
    }

    Column(
        modifier = Modifier
            .heightIn(max = 50.dp + (if (headerContent == null) 0 else 20).dp + (if (footerContent == null) 0 else 20).dp)
            .widthIn(max = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        headerContent?.let { it() }
        Box(
            modifier = containerModifier
                .aspectRatio(1f)
                .clip(CalendarTheme.shapes.itemShape)
                .clickable { onDateSelect(date) }
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(),
                color = CalendarTheme.colors.run {
                    when (state) {
                        is Today -> onTodayItemBackgroundColor
                        is Selected -> onSelectedItemBackgroundColor
                        else -> onBackgroundColor
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = textStyle
            )
        }
        footerContent?.let { it() }
    }
}

@Preview
@Composable
private fun CalenderItemViewPreview() {
    Column {
        Row(
            modifier = Modifier.width(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalenderItemView(
                date = LocalDate.now(),
                onDateSelect = {}
            )
            CalenderItemView(
                state = Selected,
                date = LocalDate.now(),
                onDateSelect = {}
            )
            CalenderItemView(
                state = Today,
                date = LocalDate.now(),
                onDateSelect = {}
            )
        }
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Row(
            modifier = Modifier.width(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalenderItemView(
                date = LocalDate.now(),
                onDateSelect = {},
                headerContent = { Text("preview") }
            )
            CalenderItemView(
                date = LocalDate.now(),
                onDateSelect = {},
                footerContent = { Text("preview") }
            )
        }
    }
}