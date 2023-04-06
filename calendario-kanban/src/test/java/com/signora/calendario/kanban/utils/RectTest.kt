package com.signora.calendario.kanban.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Test

class RectTest {
    @Test
    fun `when pointInRect is called, should return boolean`() {
        assertEquals(
            false,
            pointInRect(
                0.5f to 0.5f,
                Rect(Offset(1f, 1f), Size(3f, 3f))
            )
        )
        assertEquals(
            true,
            pointInRect(
                1f to 2f,
                Rect(Offset(1f, 1f), Size(3f, 3f))
            )
        )
    }

    @Test
    fun `when getSkylineTurningPoint is called, should get a group of turning point`() {
        assertEquals(
            listOf(
                2f to 3f, 2f to 10f, 3f to 10f, 3f to 15f, 7f to 15f,
                7f to 12f, 12f to 12f, 12f to 3f, 15f to 3f, 15f to 10f,
                20f to 10f, 20f to 8f, 24f to 8f, 24f to 3f
            ),
            getTurningPoints(
                Rect(
                    Offset(0f, 3f),
                    Size(Float.MAX_VALUE - 50f, Float.MAX_VALUE - 50f)
                ),
                listOf(
                    Rect(Offset(2f, 0f), Size(7f, 10f)),
                    Rect(Offset(3f, 0f), Size(4f, 15f)),
                    Rect(Offset(5f, 0f), Size(7f, 12f)),
                    Rect(Offset(15f, 0f), Size(5f, 10f)),
                    Rect(Offset(19f, 0f), Size(5f, 8f))
                )
            )
        )
    }

    @Test
    fun `when getSkylineTurningPoint is called under boarder, should get a group of turning point`() {
        assertEquals(
            listOf(
                5f to 15f, 7f to 15f, 7f to 12f, 12f to 12f, 12f to 3f,
                15f to 3f, 15f to 10f, 20f to 10f, 20f to 8f, 23f to 8f
            ),
            getTurningPoints(
                Rect(
                    Offset(5f, 3f),
                    Size(18f, Float.MAX_VALUE - 50f)
                ),
                listOf(
                    Rect(Offset(2f, 0f), Size(7f, 10f)),
                    Rect(Offset(3f, 0f), Size(4f, 15f)),
                    Rect(Offset(5f, 0f), Size(7f, 12f)),
                    Rect(Offset(15f, 0f), Size(5f, 10f)),
                    Rect(Offset(19f, 0f), Size(5f, 8f))
                )
            )
        )

        assertEquals(
            listOf(
                2f to 10f, 3f to 10f, 3f to 15f, 7f to 15f, 7f to 12f,
                12f to 12f, 12f to 3f, 15f to 3f, 15f to 10f, 20f to 10f,
                20f to 8f, 24f to 8f
            ),
            getTurningPoints(
                Rect(
                    Offset(2f, 3f),
                    Size(22f, Float.MAX_VALUE - 50f)
                ),
                listOf(
                    Rect(Offset(2f, 0f), Size(7f, 10f)),
                    Rect(Offset(3f, 0f), Size(4f, 15f)),
                    Rect(Offset(5f, 0f), Size(7f, 12f)),
                    Rect(Offset(15f, 0f), Size(5f, 10f)),
                    Rect(Offset(19f, 0f), Size(5f, 8f))
                )
            )
        )

        assertEquals(
            listOf(
                2f to 10f, 3f to 10f
            ),
            getTurningPoints(
                Rect(2f, 3f, 3f, Float.MAX_VALUE - 50f),
                listOf(
                    Rect(Offset(2f, 4f), Size(1f, 10f))
                )
            )
        )
    }

    @Test
    fun `when getSkyline is called, should get a group of skyline point`() {
        assertEquals(
            listOf(
                2f to 10f, 3f to 15f, 7f to 12f, 12f to 0f, 15f to 10f, 20f to 8f, 24f to 0f
            ),
            getSkyline(
                listOf(
                    Rect(Offset(2f, 0f), Size(7f, 10f)),
                    Rect(Offset(3f, 0f), Size(4f, 15f)),
                    Rect(Offset(5f, 0f), Size(7f, 12f)),
                    Rect(Offset(15f, 0f), Size(5f, 10f)),
                    Rect(Offset(19f, 0f), Size(5f, 8f))
                )
            )
        )
    }

    @Test
    fun `when getSkyline with baseline is called, should get a group of skyline point`() {
        assertEquals(
            listOf(
                2f to 10f, 3f to 15f, 7f to 12f, 12f to 3f, 15f to 10f, 20f to 8f, 24f to 3f
            ),
            getSkyline(
                listOf(
                    Rect(Offset(2f, 0f), Size(7f, 10f)),
                    Rect(Offset(3f, 0f), Size(4f, 15f)),
                    Rect(Offset(5f, 0f), Size(7f, 12f)),
                    Rect(Offset(15f, 0f), Size(5f, 10f)),
                    Rect(Offset(19f, 0f), Size(5f, 8f))
                ),
                3f
            )
        )
    }

    @Test
    fun `when getSkyline is called, should get a group of skyline point in shared region`() {
        assertEquals(
            listOf(
                1f to 3f, 2f to 0f
            ),
            getSkyline(
                listOf(
                    Rect(Offset(1f, 0f), Size(1f, 1f)),
                    Rect(Offset(1f, 0f), Size(1f, 2f)),
                    Rect(Offset(1f, 0f), Size(1f, 3f)),
                )
            )
        )
    }

    @Test
    fun `when getSkyline with baseline is called, should get a group of skyline point in shared region`() {
        assertEquals(
            listOf(
                1f to 3f, 2f to 2f
            ),
            getSkyline(
                listOf(
                    Rect(Offset(1f, 0f), Size(1f, 1f)),
                    Rect(Offset(1f, 0f), Size(1f, 2f)),
                    Rect(Offset(1f, 0f), Size(1f, 3f)),
                ),
                2f
            )
        )
    }

    @Test
    fun `when getSkyline with large baseline is called, should return none`() {
        assertEquals(
            listOf(
                0f to 5f, 1f to 5f
            ),
            getSkyline(
                listOf(Rect(Offset(0f, 0f), Size(1f, 1f))),
                5f
            )
        )
    }
}