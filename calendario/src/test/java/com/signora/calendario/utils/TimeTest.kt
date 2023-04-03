package com.signora.calendario.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.signora.calendario.models.KanbanPeriod
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import kotlin.math.round

class TimeTest {
    @Test
    fun `when getTargetScaled is called, should return a scaled base on target`() {
        val offset1hour = LocalDateTime.of(2023, 1, 1, 1, 0, 0)
        assertEquals(4.166667f, offset1hour.getTargetScaled(100f))
    }

    @Test
    fun `when getTargetScaled with week period is called, should return a scaled base on target`() {
        val offset0day = LocalDateTime.of(2023, 1, 2, 0, 0, 0)
        assertEquals(0f, offset0day.getTargetScaled(100f, KanbanPeriod.WEEK))
        val offset3day12hour = LocalDateTime.of(2023, 1, 5, 12, 0, 0)
        assertEquals(50f, offset3day12hour.getTargetScaled(100f, KanbanPeriod.WEEK))
        val offset7day = LocalDateTime.of(2023, 1, 8, 23, 59, 59)
        assertEquals(100f, round(offset7day.getTargetScaled(100f, KanbanPeriod.WEEK)))
    }

    @Test
    fun `when getOffsetSize is called, should return offset size`() {
        val (offset, size) = getOffsetSize(
            range = Pair(
                LocalDateTime.of(2023, 1, 1, 0, 0, 0),
                LocalDateTime.of(2023, 1, 1, 23, 59, 59)
            ),
            parentOffset = Offset.Zero,
            parentSize = Size(100f, 100f)
        )
        assertEquals(Offset.Zero, offset)
        assertEquals(Size(100f, 100f), size.copy(round(size.width), round(size.height)))
    }

    @Test
    fun `when getOffsetSize with week period is called, should return offset size`() {
        val (offset, size) = getOffsetSize(
            range = Pair(
                LocalDateTime.of(2023, 1, 2, 0, 0, 0),
                LocalDateTime.of(2023, 1, 8, 23, 59, 59)
            ),
            period = KanbanPeriod.WEEK,
            parentOffset = Offset.Zero,
            parentSize = Size(100f, 100f)
        )
        assertEquals(Offset.Zero, offset)
        assertEquals(Size(100f, 100f), size.copy(round(size.width), round(size.height)))
    }
}