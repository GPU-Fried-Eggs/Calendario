package com.signora.calendario.ui.pager

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
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
    flingBehavior: SnapFlingBehavior = flingBehavior(state),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((page: Int) -> Any)? = null,
    pageContent: @Composable (currentPage: Int) -> Unit
) {
    val consumeFlingNestedScrollConnection = remember(orientation) { ConsumeFlingNestedScrollConnection(orientation) }

    val density = LocalDensity.current

    LaunchedEffect(pageCount) {
        state.currentPage = minOf(pageCount - 1, state.currentPage).coerceAtLeast(0)
    }

    LaunchedEffect(density, state, pageSpacing) {
        with(density) { state.pageSpacing = pageSpacing.roundToPx() }
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

    when (orientation) {
        Orientation.Vertical -> {
            LazyColumn(
                modifier = modifier,
                state = state.lazyListState,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = Arrangement.spacedBy(pageSpacing, verticalAlignment),
                horizontalAlignment = horizontalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled
            ) {
                items(pageCount, key) { page ->
                    Box(
                        modifier = Modifier
                            .nestedScroll(consumeFlingNestedScrollConnection)
                            .fillParentMaxHeight()
                            .wrapContentSize()
                    ) {
                        pageContent(page)
                    }
                }
            }
        }
        else -> {
            LazyRow(
                modifier = modifier,
                state = state.lazyListState,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                horizontalArrangement = Arrangement.spacedBy(pageSpacing, horizontalAlignment),
                verticalAlignment = verticalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled
            ) {
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
        }
    }
}

private class ConsumeFlingNestedScrollConnection(private val orientation: Orientation) : NestedScrollConnection {
    private fun Velocity.consumeOnOrientation(orientation: Orientation): Velocity =
        if (orientation == Orientation.Vertical) copy(x = 0f) else copy(y = 0f)

    private fun Offset.consumeOnOrientation(orientation: Orientation): Offset =
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