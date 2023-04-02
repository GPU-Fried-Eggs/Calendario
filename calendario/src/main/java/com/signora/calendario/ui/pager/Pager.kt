package com.signora.calendario.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Pager(
    modifier: Modifier = Modifier,
    pageCount: Int = 3,
    state: PagerState,
    pageSpacing: Dp = 0.dp,
    orientation: Orientation = Orientation.Horizontal,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((page: Int) -> Any)? = null,
    pageContent: @Composable (currentPage: Int) -> Unit
) {
    val consumeFlingNestedScrollConnection = remember(orientation) { ConsumeFlingNestedScrollConnection(orientation) }

    LaunchedEffect(pageCount) {
        state.currentPage = minOf(pageCount - 1, state.currentPage).coerceAtLeast(0)
    }

    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { !it }.drop(1)
            .collect { state.onScrollFinished() }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.currentLayoutPageInfo }
            .filter { !state.isScrollInProgress }
            .collect { state.updateCurrentPageBasedOnLazyListState() }
    }

    val pagerItems: (LazyListScope.() -> Unit) = {
        items(pageCount, key) { page ->
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
            modifier = modifier,
            state = state.lazyListState,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = Arrangement.spacedBy(pageSpacing, verticalAlignment),
            horizontalAlignment = horizontalAlignment,
            flingBehavior = rememberSnapFlingBehavior(state.lazyListState),
            userScrollEnabled = userScrollEnabled,
            content = pagerItems
        )
    } else {
        LazyRow(
            modifier = modifier,
            state = state.lazyListState,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            horizontalArrangement = Arrangement.spacedBy(pageSpacing, horizontalAlignment),
            verticalAlignment = verticalAlignment,
            flingBehavior = rememberSnapFlingBehavior(state.lazyListState),
            userScrollEnabled = userScrollEnabled,
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