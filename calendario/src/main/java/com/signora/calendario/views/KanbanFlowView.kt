package com.signora.calendario.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import com.signora.calendario.models.DefaultKanbanTask
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.utils.*
import java.time.LocalDateTime
import java.util.*
import kotlin.math.min

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun KanbanFlowView(
    taskPlanList: List<KanbanTask>,
    taskDoneList: List<KanbanTask>
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {

                    }
                )
            },
    ) {
        // left panel
        calculateTaskRect(
            tasks = taskPlanList,
            parentOffset = Offset.Zero,
            parentSize = size.copy(width = size.width / 2f)
        ).forEach {
            drawTask(
                task = it.key,
                parentRect = it.value,
                textMeasurer = textMeasurer
            )
        }

        // divide line
        drawLine(
            color = Color.Gray,
            start = center.copy(y = 10f),
            end = center.copy(y = size.height - 10f)
        )

        // right panel
        calculateTaskRect(
            tasks = taskDoneList,
            parentOffset = center.copy(y = 0f),
            parentSize = size.copy(width = size.width / 2f)
        ).forEach { (task, rect) ->
            drawTask(
                task = task,
                parentRect = rect,
                textMeasurer = textMeasurer
            )
        }
    }
}

private fun calculateTaskRect(tasks: List<KanbanTask>, parentOffset: Offset, parentSize: Size): Map<KanbanTask, Rect> {
    val taskRectList = tasks
        .sortedBy { it.range.first }
        .fold(mutableMapOf<KanbanTask, Rect>()) { prevAnalyzed, task ->
            val currentTargetRect = getRect(parentOffset, parentSize, task.range)
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
                // Use greedy algorithm filter the best position of new rect
                val availableLines =
                    getSkylineTurningPoint(currentTargetRect, prevAnalyzed.values.map { it.copy(top = 0f) })
                        .filter { it.second == currentTargetRect.top } // Equal to baseline
                        .zipWithNext()
                        .sortedWith { (aStart, aEnd), (bStart, bEnd) ->
                            ((aEnd.first - aStart.first) - (bEnd.first - bStart.first)).toInt()
                        }

                availableLines.firstOrNull()?.let { (left, right) ->
                    prevAnalyzed[task] = Rect(
                        left.first, left.second, right.first, left.second + currentTargetRect.height
                    )
                } ?: run {
                    val recomposeBox = listOf(task to currentTargetRect) + overlapping
                    val sharedWidth = parentSize.width / recomposeBox.size

                    recomposeBox
                        .reversed()
                        .forEachIndexed { index, (childTask, childRect) ->
                            prevAnalyzed[childTask] = Rect(
                                // Each box should be placed strictly (space-evenly) by shared width
                                childRect.topLeft.copy(currentTargetRect.topLeft.x + sharedWidth * index),
                                // This parent box shouldn't over the shared grid
                                childRect.size.copy(min(sharedWidth, childRect.size.width))
                            )
                        }
                }
            } else {
                prevAnalyzed[task] = currentTargetRect
            }

            prevAnalyzed
        }

    return taskRectList.toMap()
}

private fun DrawScope.drawPopup() {

}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTask(
    task: KanbanTask,
    parentRect: Rect,
    textMeasurer: TextMeasurer
) {
    drawRoundRect(
        color = task.color,
        topLeft = parentRect.topLeft,
        size = parentRect.size,
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawText(
        textMeasurer = textMeasurer,
        text = task.payload,
        topLeft = parentRect.topLeft,
        size = parentRect.size
    )
}

@OptIn(ExperimentalTextApi::class)
@Deprecated("drawTaskRawData will draw task with no recompose behaviour, override previous task directly.")
private fun DrawScope.drawTaskRawData(
    task: KanbanTask,
    parentRect: Rect,
    textMeasurer: TextMeasurer
) {
    val (childOffset, childSize) = getOffsetSize(
        range = task.range,
        parentOffset = parentRect.topLeft,
        parentSize = parentRect.size
    )
    drawTask(task, Rect(childOffset, childSize), textMeasurer)
}

@Preview
@Composable
private fun KanbanFlowViewPreview() {
    KanbanFlowView(
        taskPlanList = (0..23).map {
            DefaultKanbanTask(
                Pair(
                    LocalDateTime.of(2023, 1, 1, it, 0, 0),
                    LocalDateTime.of(2023, 1, 1, it, 59, 59)
                ),
                color = generateRandomColor("100"),
                payload = "Preview($it:00-$it:59)"
            )
        },
        taskDoneList = (8..16).map {
            if (it % 2 == 0) {
                DefaultKanbanTask(
                    Pair(
                        LocalDateTime.of(2023, 1, 1, it, 0, 0),
                        LocalDateTime.of(2023, 1, 1, it + 2, 0, 0)
                    ),
                    color = generateRandomColor("100"),
                    payload = "Preview(${it}:00-${it + 2}:00)"
                )
            } else {
                DefaultKanbanTask(
                    Pair(
                        LocalDateTime.of(2023, 1, 1, it, 30, 0),
                        LocalDateTime.of(2023, 1, 1, it + 2, 30, 0)
                    ),
                    color = generateRandomColor("100"),
                    payload = "Preview($it:30-${it + 2}:30)"
                )
            }
        }
    )
}