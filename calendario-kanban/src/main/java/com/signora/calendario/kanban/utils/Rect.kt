package com.signora.calendario.kanban.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.signora.calendario.kanban.enums.KanbanPeriod
import com.signora.calendario.kanban.enums.KanbanTaskType
import com.signora.calendario.kanban.models.KanbanTask
import java.time.LocalDateTime
import java.util.PriorityQueue
import kotlin.math.min

internal fun pointInRect(point: Pair<Float, Float>, rect: Rect): Boolean {
    data class Vector(val x: Float, val y: Float) {
        constructor(head: Pair<Float, Float>, tail: Pair<Float, Float>) : this(tail.first - head.first, tail.second - head.second)
        fun dot(other: Vector): Float = x * other.x + y * other.y
    }
    val left = Vector(rect.left to rect.bottom, rect.left to rect.top)
    val bottom = Vector(rect.left to rect.bottom, rect.right to rect.bottom)
    val topRay = Vector(rect.left to rect.top, point)
    val lowRay = Vector(rect.left to rect.bottom, point)
    return lowRay.dot(left) in 0.0..left.dot(left).toDouble() && topRay.dot(bottom) in 0.0..bottom.dot(bottom).toDouble()
}

internal fun calculateTaskRect(
    tasks: List<KanbanTask>,
    type: KanbanTaskType,
    parentOffset: Offset,
    parentSize: Size
): Map<KanbanTask, Rect> {
    val taskRectList = tasks
        .sortedBy { it.range[type]?.first }
        .fold(mutableMapOf<KanbanTask, Rect>()) { prevAnalyzed, task ->
            task.range[type]?.let { range ->
                val currentTargetRect = getRect(parentOffset, parentSize, range)
                val prevAnalyzedList = prevAnalyzed.toList()

                val overlapping = prevAnalyzedList
                    .foldRight(
                        mutableListOf<Pair<KanbanTask, Rect>>()
                    ) { (prevTask, prevRect), currentOverlap ->
                        if (currentTargetRect.overlaps(prevRect))
                            currentOverlap.add(prevTask to prevRect)
                        currentOverlap
                    }

                if (overlapping.isNotEmpty()) {
                    // Get sky line method to find out key points
                    val keyPoints = getTurningPoints(
                        parentRect = currentTargetRect,
                        overlapped = prevAnalyzed.values.map { it.copy(top = 0f) }
                    )
                    // Use greedy algorithm filter the best position of new rect
                    val availableLines = keyPoints
                        .filter { it.second == currentTargetRect.top } // Equal to baseline
                        .zipWithNext()
                        .sortedWith { (aStart, aEnd), (bStart, bEnd) ->
                            ((aEnd.first - aStart.first) - (bEnd.first - bStart.first)).toInt()
                        }

                    availableLines.firstOrNull()?.let { (left, right) ->
                        prevAnalyzed[task] = Rect(
                            left = left.first,
                            top = left.second,
                            right = right.first,
                            bottom = left.second + currentTargetRect.height
                        )
                    } ?: run {
                        val recomposeBox = listOf(task to currentTargetRect) + overlapping
                        val sharedWidth = parentSize.width / recomposeBox.size

                        recomposeBox
                            .reversed()
                            .forEachIndexed { index, (childTask, childRect) ->
                                prevAnalyzed[childTask] = Rect(
                                    // Each box should be placed strictly (space-evenly)
                                    childRect.topLeft.copy(
                                        currentTargetRect.topLeft.x + sharedWidth * index
                                    ),
                                    // This parent box shouldn't over the shared grid
                                    childRect.size.copy(
                                        min(sharedWidth, childRect.size.width)
                                    )
                                )
                            }
                    }
                } else {
                    prevAnalyzed[task] = currentTargetRect
                }
            }

            prevAnalyzed
        }

    return taskRectList.toMap()
}

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
        left = parentOffset.x,
        top = range.first.getTargetScaled(parentSize.height, period),
        right = parentOffset.x + parentSize.width,
        bottom = range.second.getTargetScaled(parentSize.height, period)
    )
}

internal fun getTurningPoints(parentRect: Rect, overlapped: List<Rect>): List<Pair<Float, Float>> {
    val baseline = parentRect.top // The lower bar of parent

    val points = getSkyline(overlapped, baseline)
        .fold(mutableListOf<Pair<Float, Float>>()) { collected, point ->
            when {
                point.first <= parentRect.left -> {
                    when (val index = collected.indexOfFirst { it.first == parentRect.left }) {
                        -1 -> collected.add(parentRect.left to point.second)
                        else -> collected[index] = parentRect.left to point.second
                    }
                }
                point.first >= parentRect.right -> {
                    when (val index = collected.indexOfFirst { it.first == parentRect.right }) {
                        -1 -> collected.add(parentRect.right to collected.last().second)
                        else -> collected[index] = parentRect.right to point.second
                    }
                }
                else -> {
                    collected.add(point.first to (collected.lastOrNull()?.second ?: baseline))
                    collected.add(point)
                }
            }

            collected
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