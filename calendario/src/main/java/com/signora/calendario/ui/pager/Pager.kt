package com.signora.calendario.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pager(
    pageCount: Int = 3,
    pagerState: PagerState,
    orientation: Orientation = Orientation.Horizontal,
    pageContent: @Composable (currentPage: Int) -> Unit
) {
    val consumeFlingNestedScrollConnection = remember(orientation) { ConsumeFlingNestedScrollConnection(orientation) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .filter { !it }
            .drop(1)
            .collect { pagerState.onScrollFinished() }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentLayoutPageInfo }
            .filter { !pagerState.isScrollInProgress }
            .collect { pagerState.updateCurrentPageBasedOnLazyListState() }
    }

    val pagerItems: (LazyListScope.() -> Unit) = {
        items(pageCount) { page ->
            Box(
                modifier = Modifier
                    .nestedScroll(consumeFlingNestedScrollConnection)
                    .fillParentMaxWidth()
                    .wrapContentSize()
            ) {
                pageContent(page)
            }
        }
    }

    if (orientation == Orientation.Vertical) {
        LazyColumn(
            state = pagerState.lazyListState,
            flingBehavior = rememberSnapFlingBehavior(pagerState.lazyListState),
            content = pagerItems
        )
    } else {
        LazyRow(
            state = pagerState.lazyListState,
            flingBehavior = rememberSnapFlingBehavior(pagerState.lazyListState),
            content = pagerItems
        )
    }
}

private class ConsumeFlingNestedScrollConnection(val orientation: Orientation) : NestedScrollConnection {
    fun Velocity.consumeOnOrientation(orientation: Orientation): Velocity =
        if (orientation == Orientation.Vertical) copy(x = 0f) else copy(y = 0f)

    fun Offset.consumeOnOrientation(orientation: Orientation): Offset =
        if (orientation == Orientation.Vertical) copy(x = 0f) else copy(y = 0f)

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        return when (source) {
            NestedScrollSource.Fling -> available.consumeOnOrientation(orientation)
            else -> Offset.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return available.consumeOnOrientation(orientation)
    }
}