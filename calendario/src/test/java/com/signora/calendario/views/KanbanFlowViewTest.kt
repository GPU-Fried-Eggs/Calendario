package com.signora.calendario.views

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.signora.calendario.utils.getSkylineTurningPoint
import org.junit.Assert
import org.junit.Test

class KanbanFlowViewTest {
    @Test
    fun `test calculateTaskRect calculate longest line`() {
        // test for longestLine of [calculateTaskRect]
        Assert.assertEquals(
            listOf((12f to 3f) to (15f to 3f)),
            getSkylineTurningPoint(
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
            .filter { it.second == 3f } // Equal to baseline
            .zipWithNext()
            .sortedWith { (aStart, aEnd), (bStart, bEnd) ->
                ((aEnd.first - aStart.first) - (bEnd.first - bStart.first)).toInt()
            }
        )
    }

    @Test
    fun `test calculateTaskRect calculate longest line overlap`() {
        Assert.assertEquals(
            listOf<Pair<Pair<Float, Float>, Pair<Float, Float>>>(),
            getSkylineTurningPoint(
                Rect(
                    Offset(1f, 1f),
                    Size(1f, Float.MAX_VALUE - 50f)
                ),
                listOf(
                    Rect(Offset(1f, 0f), Size(1f, 1f)),
                    Rect(Offset(1f, 0f), Size(1f, 2f)),
                    Rect(Offset(1f, 0f), Size(1f, 3f)),
                )
            )
            .filter { it.second == 1f } // Equal to baseline
            .zipWithNext()
            .sortedWith { (aStart, aEnd), (bStart, bEnd) ->
                ((aEnd.first - aStart.first) - (bEnd.first - bStart.first)).toInt()
            }
        )
    }

    @Test
    fun `test calculateTaskRect calculate longest line real live`() {
        Assert.assertEquals(
            listOf<Pair<Pair<Float, Float>, Pair<Float, Float>>>(),
            getSkylineTurningPoint(
                Rect(Offset(540f, 789.2f), Size(540f, 137.3f)),
                listOf(
                    Rect(Offset(540f, 686.3f), Size(540f, 137.3f)).copy(top = 0f),
                )
            )
            .filter { it.second == 789.2f } // Equal to baseline
            .zipWithNext()
            .sortedWith { (aStart, aEnd), (bStart, bEnd) ->
                ((aEnd.first - aStart.first) - (bEnd.first - bStart.first)).toInt()
            }
        )
    }
}