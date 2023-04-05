package com.signora.calendario.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.signora.calendario.models.KanbanPeriod
import java.time.LocalDateTime
import java.util.*

internal fun getOffsetSize(
    parentOffset: Offset,
    parentSize: Size,
    range: Pair<LocalDateTime, LocalDateTime>,
    period: KanbanPeriod = KanbanPeriod.DAY
): Pair<Offset, Size> {
    val start = range.first.getTargetScaled(parentSize.height, period)
    val end = range.second.getTargetScaled(parentSize.height, period)

    // inherit from parent offset
    return Offset(parentOffset.x, start) to Size(parentSize.width, end - start)
}

internal fun getRect(
    parentOffset: Offset,
    parentSize: Size,
    range: Pair<LocalDateTime, LocalDateTime>,
    period: KanbanPeriod = KanbanPeriod.DAY
): Rect {
    return Rect(
        parentOffset.x,
        range.first.getTargetScaled(parentSize.height, period),
        parentOffset.x + parentSize.width,
        range.second.getTargetScaled(parentSize.height, period)
    )
}

internal fun getSkylineTurningPoint(parentRect: Rect, overlapped: List<Rect>): List<Pair<Float, Float>> {
    val baseline = parentRect.top // The lower bar of parent

    val points = getSkyline(overlapped, baseline)
        .fold(
            mutableListOf<Pair<Float, Float>>()
        ) { accumulatePoints, point ->
            when {
                point.first <= parentRect.left -> {
                    when (val index = accumulatePoints.indexOfFirst { it.first == parentRect.left }) {
                        -1 -> accumulatePoints.add(parentRect.left to point.second)
                        else -> accumulatePoints[index] = parentRect.left to point.second
                    }
                }
                point.first >= parentRect.right -> {
                    when (val index = accumulatePoints.indexOfFirst { it.first == parentRect.right }) {
                        -1 -> accumulatePoints.add(parentRect.right to accumulatePoints.last().second)
                        else -> accumulatePoints[index] = parentRect.right to point.second
                    }
                }
                else -> {
                    accumulatePoints.add(point.first to (accumulatePoints.lastOrNull()?.second ?: baseline))
                    accumulatePoints.add(point)
                }
            }

            accumulatePoints
        }
        .distinct()

    return points
}

internal fun getSkyline(overlapped: List<Rect>, baseline:Float = 0f): List<Pair<Float, Float>> {
    val boxHeap = PriorityQueue<Rect>(compareByDescending { it.height })
    val skyline = mutableListOf<Pair<Float, Float>>()

    overlapped.forEach {
        boxHeap.processEndings(it.left, skyline)
        if (boxHeap.isEmpty() || it.height > requireNotNull(boxHeap.peek()).height)
            skyline.add(it.left to it.height)
        boxHeap.offer(it)
    }

    boxHeap.processEndings(null, skyline)

    return skyline
        .groupBy({ it.first }) { it.second }
        .entries.map { it.key to (it.value + baseline).max() }
}

private fun PriorityQueue<Rect>.processEndings(head: Float?, skyline: MutableList<Pair<Float, Float>>) {
    while (!isEmpty() && (head == null || requireNotNull(peek()).right < head)) {
        val endedBuilding = remove()
        val endedRight = endedBuilding.right
        val endedHeight = endedBuilding.height

        while (!isEmpty() && requireNotNull(peek()).right <= endedRight) remove()

        val height = if (isEmpty()) 0f else requireNotNull(peek()).height
        if (height != endedHeight) skyline.add(endedRight to height)
    }
}