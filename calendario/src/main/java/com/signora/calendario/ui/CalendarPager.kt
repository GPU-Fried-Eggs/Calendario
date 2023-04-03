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

@Composable
fun <T> CalendarPager(
    loadedDate: Array<T>,
    middleIndex: Int = loadedDate.size / 2, // TODO: Auto Even -> Odd feature
    loadPrevDates: (T) -> Unit = {},
    loadNextDates: (T) -> Unit = {},
    orientation: Orientation = Orientation.Horizontal,
    pageContent: @Composable (currentPage: Int) -> Unit
) {
    val pagerState = rememberSaveable(saver = PagerState.Saver) { PagerState(middleIndex) }

    LaunchedEffect(pagerState.currentPage) {
        pagerState.currentPage.let {
            when (it) {
                in 0 until middleIndex -> loadPrevDates(loadedDate[it])
                in middleIndex + 1..loadedDate.size -> loadNextDates(loadedDate[it])
            }
        }
    }

    LaunchedEffect(loadedDate) {
        pagerState.scrollToPage(middleIndex)
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
    CalendarPager(arrayOf(0, 1, 2)){
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