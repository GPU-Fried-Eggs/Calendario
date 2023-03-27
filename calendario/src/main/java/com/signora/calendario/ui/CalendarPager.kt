package com.signora.calendario.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarPager(
    loadedDates: List<List<LocalDate>>,
    loadNextDates: (LocalDate) -> Unit,
    loadPrevDates: (LocalDate) -> Unit,
    content: @Composable (currentPage: Int) -> Unit
) {
    var initialized by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(1)

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 2) {
            loadNextDates(loadedDates[1][0])
            pagerState.scrollToPage(1)
        }
        if (pagerState.currentPage == 0 && initialized) {
            loadPrevDates(loadedDates[0][0])
            pagerState.scrollToPage(1)
        }
    }

    LaunchedEffect(Unit) {
        initialized = true
    }

    HorizontalPager(
        pageCount = 3,
        state = pagerState,
        verticalAlignment = Alignment.Top,
        pageContent = content
    )
}

@Preview
@Composable
private fun CalendarPagerPreview() {
    val dates = List(7 * 3) {
        LocalDate.now().run {
            var date = this
            while (date.dayOfWeek != DayOfWeek.MONDAY) {
                date = date.minusDays(1)
            }
            date
        }.minusWeeks(1).plusDays(it.toLong())
    }

    CalendarPager(
        loadedDates = List(3) {
            dates.slice(it * 7 until (it + 1) * 7)
        },
        loadNextDates = {},
        loadPrevDates = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "preview",
                style = MaterialTheme.typography.h2
            )
        }
    }
}