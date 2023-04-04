package com.signora.calendario.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.signora.calendario.ui.pager.Pager
import com.signora.calendario.ui.pager.PagerState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PagerStateTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun scroll_and_change_page_count() {
        val pagerState = PagerState()
        var count by mutableStateOf(10)

        composeTestRule.setContent {
            Pager(
                pageCount = count,
                state = pagerState
            ) { page ->
                BasicText(text = "Page:$page")
            }
        }
        composeTestRule.waitForIdle()

        // scroll to page 8
        composeTestRule.runOnIdle {
            runBlocking {
                pagerState.scrollToPage(8)
            }
        }
        assertEquals(8, pagerState.currentPage)
        assertEquals(10, pagerState.pageCount)

        // Change page count
        count = 4
        composeTestRule.waitForIdle()
        assertEquals(3, pagerState.currentPage)
        assertEquals(4, pagerState.pageCount)

        // Change page count to 0 will trigger launch effect
        count = 0
        composeTestRule.waitForIdle()
        assertEquals(pagerState.currentPage, 0)
        assertEquals(pagerState.pageCount, 0)
    }
}