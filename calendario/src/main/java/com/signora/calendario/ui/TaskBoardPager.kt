package com.signora.calendario.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import com.signora.calendario.ui.pager.Pager
import com.signora.calendario.ui.pager.PagerState
import java.time.LocalDateTime

@Composable
fun TaskBoardPager(
    pageCount: Int = 3,
    orientation: Orientation = Orientation.Vertical,
    loadedDates: Array<LocalDateTime>,
    loadPrevDates: (LocalDateTime) -> Unit,
    loadNextDates: (LocalDateTime) -> Unit,
    pageContent: @Composable (currentPage: Int) -> Unit
) {
    val pagerState = rememberSaveable(saver = PagerState.Saver) { PagerState(1) }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 0) {
            loadPrevDates(loadedDates[0])
            pagerState.scrollToPage(1)
        }
        if (pagerState.currentPage == 2) {
            loadNextDates(loadedDates[2])
            pagerState.scrollToPage(1)
        }
    }

    // TODO
}
