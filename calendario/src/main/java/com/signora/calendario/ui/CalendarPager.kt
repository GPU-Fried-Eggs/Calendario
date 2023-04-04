package com.signora.calendario.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.ui.pager.Pager
import com.signora.calendario.ui.pager.PagerState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> CalendarPager(
    loadedDate: Array<T>,
    loadPrevDates: (T) -> Unit = {},
    loadNextDates: (T) -> Unit = {},
    orientation: Orientation = Orientation.Horizontal,
    pageContent: @Composable (currentPage: Int) -> Unit
) {
    val pagerState = rememberSaveable(saver = PagerState.Saver) { PagerState(loadedDate.size / 2) }
    var page by remember { mutableStateOf(-1) }

    LaunchedEffect(pagerState.currentPage) {
        if (page != pagerState.currentPage) {
            page = pagerState.currentPage
            when (page) {
                in 0 until loadedDate.size / 2 -> loadPrevDates(loadedDate[page])
                in (loadedDate.size / 2) + 1..loadedDate.size -> loadNextDates(loadedDate[page])
            }
        }
    }

    LaunchedEffect(loadedDate) {
        pagerState.scrollToPage(loadedDate.size / 2)
    }

    Pager(
        pageCount = loadedDate.size,
        state = pagerState,
        orientation = orientation,
        pageContent = pageContent
    )
}

@Preview
@Composable
private fun CalendarPagerPreview() {
    CalendarPager(arrayOf(0, 1, 2)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "preview page $it",
                style = MaterialTheme.typography.h3
            )
        }
    }
}