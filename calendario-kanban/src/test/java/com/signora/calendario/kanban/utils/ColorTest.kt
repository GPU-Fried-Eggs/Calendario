package com.signora.calendario.kanban.utils

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

class ColorTest {
    @Test
    fun `when getBuildInColor is called, should return a array of Color`() {
        assertArrayEquals(
            arrayOf(
                0xFFE57373, 0xFFF06292, 0xFFBA68C8, 0xFF9575CD, 0xFF7986CB, 0xFF64B5F6,
                0xFF4FC3F7, 0xFF4DD0E1, 0xFF4DB6AC, 0xFF81C784, 0xFFAED581, 0xFFDCE775,
                0xFFFFF176, 0xFFFFD54F, 0xFFFFB74D, 0xFFFF8A65, 0xFFA1887F, 0xFF90A4AE
            ).map { Color(it) }.toTypedArray(),
            getBuildInColor()
        )
    }

    @Test
    fun `when generateRandomColor is called, should return a array of Color`() {
        assert(
            generateRandomColor() in arrayOf(
                0xFFE57373, 0xFFF06292, 0xFFBA68C8, 0xFF9575CD, 0xFF7986CB, 0xFF64B5F6,
                0xFF4FC3F7, 0xFF4DD0E1, 0xFF4DB6AC, 0xFF81C784, 0xFFAED581, 0xFFDCE775,
                0xFFFFF176, 0xFFFFD54F, 0xFFFFB74D, 0xFFFF8A65, 0xFFA1887F, 0xFF90A4AE
            ).map { Color(it) }.toTypedArray()
        )
    }
}