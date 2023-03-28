package com.signora.calendario.ui.pager

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

@Stable
class PagerState(initialPage: Int = 0) : ScrollableState {
    val lazyListState = LazyListState(firstVisibleItemIndex = initialPage)

    var animationTargetPage: Int? by mutableStateOf(null)

    var currentPage by mutableStateOf(initialPage)

    val currentLayoutPageInfo: LazyListItemInfo?
        get() = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull { it.offset <= 0 }

    suspend fun scrollToPage(page: Int, pageOffset: Float = 0f) {
        try {
            animationTargetPage = page

            // First scroll to the given page. It will now be laid out at offset 0
            lazyListState.scrollToItem(index = page)

            // If we have a start spacing, we need to offset (scroll) by that too
            if (pageOffset > 0.0001f) {
                currentLayoutPageInfo?.let {
                    scroll {
                        scrollBy(it.size * pageOffset)
                    }
                }
            }
        } finally {
            onScrollFinished()
        }
    }

    internal fun updateCurrentPageBasedOnLazyListState() {
        currentLayoutPageInfo?.let{ currentPage = it.index }
    }

    internal fun onScrollFinished() {
        updateCurrentPageBasedOnLazyListState()
        animationTargetPage = null
    }

    override suspend fun scroll(scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit) {
        lazyListState.scroll(scrollPriority, block)
    }

    override fun dispatchRawDelta(delta: Float): Float {
        return lazyListState.dispatchRawDelta(delta)
    }

    override val isScrollInProgress: Boolean
        get() = lazyListState.isScrollInProgress

    companion object {
        val Saver: Saver<PagerState, *> = listSaver(
            save = { listOf<Any>(it.currentPage) },
            restore = { PagerState(it[0] as Int) }
        )
    }
}