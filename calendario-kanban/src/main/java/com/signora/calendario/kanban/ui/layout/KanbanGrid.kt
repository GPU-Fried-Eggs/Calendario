package com.signora.calendario.kanban.ui.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.signora.calendario.kanban.R
import com.signora.calendario.kanban.enums.KanbanTaskType
import com.signora.calendario.kanban.models.DefaultKanbanTask
import com.signora.calendario.kanban.models.KanbanTask
import com.signora.calendario.kanban.models.KanbanTaskRange
import com.signora.calendario.kanban.utils.calculateTaskRect
import com.signora.calendario.kanban.utils.generateRandomColor
import com.signora.calendario.kanban.utils.getOffsetSize
import com.signora.calendario.kanban.utils.pointInRect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val ContentRadiusSize = 12f

@Composable
internal fun KanbanGrid(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    // period: KanbanPeriod = KanbanPeriod.DAY,
    scalarWidth: Dp = 52.dp,
    taskList: Map<LocalDate, List<KanbanTask>>,
    onTaskListUpdate: (LocalDate, List<KanbanTask>) -> Unit
) {
    var selectedTask by remember { mutableStateOf<KanbanTask?>(null) }

    Column(modifier) {
        KanbanGridTitle(scalarWidth)
        taskList[selectedDate]?.let {
            KanbanGridBody(
                scalar = (0..23).map { hour ->
                    selectedDate.atTime(hour, 0, 0)
                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                },
                scalarWidth = scalarWidth,
                taskList = it,
                onTaskClick = { task ->
                    selectedTask = task
                }
            )
        }
        selectedTask?.let {
            KanbanPopup(
                task = it,
                onUpdate = { modified ->
                    onTaskListUpdate(selectedDate, taskList[selectedDate]?.let { taskList ->
                        taskList.toMutableList().apply {
                            val index = indexOf(it)
                            if (index > 0) {
                                this[indexOf(it)] = modified
                            } else {
                                add(modified)
                            }
                        }
                    } ?: listOf(modified))
                },
                onExist = {
                    selectedTask = null
                }
            )
        }
        // LazyColumn(
        //     modifier = Modifier
        //         .fillMaxSize()
        //         //.verticalScroll(state)
        // ) {
        //     taskList.forEach { (date, taskInfo) ->
        //        itemsIndexed(
        //            items = taskInfo,
        //            key = { index, item -> "$index:${item.hashCode()}" }
        //        ) { index, item ->
        //        }
        //     }
        // }
    }
}

@Composable
private fun KanbanGridTitle(scalarWidth: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(scalarWidth))
        Text(
            text = stringResource(R.string.kanban_plan),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Text(
            text = stringResource(R.string.kanban_done),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun KanbanPopup(task: KanbanTask, onUpdate: (KanbanTask) -> Unit, onExist: () -> Unit) {
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onExist,
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onUpdate(task)
                        onExist()
                    },
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(text = "Save")
                }
                Spacer(
                    modifier = Modifier.padding(4.dp)
                )
                Button(
                    onClick = onExist,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(text = "Cancel")
                }
            }
        },
        title = {
            Text(text = "2222")
        },
        text = {
            Column() {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it},
                    label = { Text("Enter text") },
                    minLines = 1,
                )
            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun KanbanGridBody(
    scalar: List<String>,
    scalarWidth: Dp,
    taskList: List<KanbanTask>,
    onTaskClick: (KanbanTask) -> Unit
) {
    val taskRectMap = remember { mutableStateMapOf<KanbanTaskType, Map<KanbanTask, Rect>>() }
    var canvasSize by remember { mutableStateOf(Int.MIN_VALUE to Int.MIN_VALUE) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(canvasSize, taskList) {
        KanbanTaskType.values().run {
            val width = (canvasSize.first / size).toFloat()
            val height = canvasSize.second.toFloat()
            forEachIndexed { index, type ->
                launch {
                    taskRectMap[type] = calculateTaskRect(
                        tasks = taskList,
                        type = type,
                        parentOffset = Offset(width * index, 0f),
                        parentSize = Size(width, height)
                    )
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(1024.dp)
    ) {
        Column(
            modifier = Modifier
                .width(scalarWidth)
                .fillMaxHeight()
                .padding(horizontal = 6.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            scalar.forEach { text ->
                BasicText(
                    text = text,
                    maxLines = 1
                )
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    canvasSize = it.width to it.height
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            KanbanTaskType
                                .values()
                                .run {
                                    val width = canvasSize.first / size
                                    forEachIndexed { index, type ->
                                        val headOffsetX = width * index
                                        val tailOffsetX = width * (index + 1)
                                        if (it.x.toInt() in headOffsetX..tailOffsetX) {
                                            taskRectMap[type]?.forEach { (kanbanTask, rect) ->
                                                if (pointInRect(it.x to it.y, rect)) {
                                                    onTaskClick(kanbanTask)
                                                    return@run
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                    )
                },
        ) {
            // left panel
            taskRectMap[KanbanTaskType.PLAN]?.run {
                forEach { (task, rect) ->
                    drawTask(
                        task = task,
                        parentRect = rect,
                        textMeasurer = textMeasurer
                    )
                }
            }

            // divide line
            drawLine(
                color = Color.Gray,
                start = center.copy(y = 10f),
                end = center.copy(y = size.height - 10f)
            )

            // right panel
            taskRectMap[KanbanTaskType.DONE]?.run {
                forEach { (task, rect) ->
                    drawTask(
                        task = task,
                        parentRect = rect,
                        textMeasurer = textMeasurer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTask(task: KanbanTask, parentRect: Rect, textMeasurer: TextMeasurer) {
    drawRoundRect(
        color = task.color,
        topLeft = parentRect.topLeft,
        size = parentRect.size,
        cornerRadius = CornerRadius(ContentRadiusSize, ContentRadiusSize)
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
    type: KanbanTaskType = KanbanTaskType.PLAN,
    parentRect: Rect,
    textMeasurer: TextMeasurer
) {
    task.range[type]?.let { range ->
        val (childOffset, childSize) = getOffsetSize(
            range = range,
            parentOffset = parentRect.topLeft,
            parentSize = parentRect.size
        )
        drawTask(task, Rect(childOffset, childSize), textMeasurer)
    }
}

@Preview
@Composable
private fun KanbanPopupPreview() {
    KanbanPopup(
        task = DefaultKanbanTask(
            KanbanTaskRange(LocalDateTime.now() to LocalDateTime.now()),
            payload = ""
        ),
        onUpdate = {},
        onExist = {}
    )
}

@Preview
@Composable
private fun KanbanGridPreview() {
    val selectedDate = remember { LocalDate.now().plusDays((0..3).random().toLong()) }
    val taskPool = remember {
        mutableStateMapOf<LocalDate, List<KanbanTask>>(*(Array(24) {
            selectedDate to DefaultKanbanTask(
                KanbanTaskRange(
                    selectedDate.atTime(it, 0, 0) to selectedDate.atTime(it, 59, 59),
                    if (it in 8..16)
                        selectedDate.atTime(it, if (it % 2 == 0) 0 else 30, 0) to
                                selectedDate.atTime(it + 2, if (it % 2 == 0) 0 else 30, 0)
                    else null
                ),
                color = generateRandomColor("100"),
                payload = "Preview($it:00-$it:59)"
            )
        }.groupBy({ it.first }) { it.second }.toList().toTypedArray()))
    }

    KanbanGrid(
        selectedDate = selectedDate,
        taskList = taskPool
    ) { _, _ ->

    }
}