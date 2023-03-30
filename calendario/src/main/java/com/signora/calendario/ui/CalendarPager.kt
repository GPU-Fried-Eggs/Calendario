package com.signora.calendario.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.ui.pager.Pager
import com.signora.calendario.ui.pager.PagerState
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarPager(
    pageCount: Int = 3,
    orientation: Orientation = Orientation.Horizontal,
    loadedMonthWeek: Array<Pair<YearMonth, Pair<LocalDate, LocalDate>?>>,
    loadPrevDates: (Pair<YearMonth, Pair<LocalDate, LocalDate>?>) -> Unit,
    loadNextDates: (Pair<YearMonth, Pair<LocalDate, LocalDate>?>) -> Unit,
    pageContent: @Composable (currentPage: Int) -> Unit
) {
    val pagerState = rememberSaveable(saver = PagerState.Saver) { PagerState(1) }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 0) {
            loadPrevDates(loadedMonthWeek[0])
            pagerState.scrollToPage(1)
        }
        if (pagerState.currentPage == 2) {
            loadNextDates(loadedMonthWeek[2])
            pagerState.scrollToPage(1)
        }
    }

    Pager(
        pageCount = pageCount,
        pageState = pagerState,
        orientation = orientation,
        pageContent = pageContent
    );
}

@Preview
@Composable
private fun CalendarPagerPreview() {
    CalendarPager(
        loadedMonthWeek = Array(3) {
            Pair(YearMonth.now(), Pair(LocalDate.now(), LocalDate.now()))
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