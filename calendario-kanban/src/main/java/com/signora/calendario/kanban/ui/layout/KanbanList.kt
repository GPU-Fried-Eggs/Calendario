package com.signora.calendario.kanban.ui.layout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.kanban.models.DefaultKanbanTask
import com.signora.calendario.kanban.models.KanbanTask
import com.signora.calendario.kanban.models.KanbanTaskRange
import com.signora.calendario.kanban.models.NestedKanbanTask
import com.signora.calendario.kanban.utils.generateRandomColor
import com.signora.calendario.kanban.views.ItemViewDefaults
import com.signora.calendario.kanban.views.KanbanTaskView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.reflect.KClass

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KanbanList(
    modifier: Modifier = Modifier,
    taskViews: Map<KClass<out KanbanTask>, KanbanTaskView> = ItemViewDefaults.taskItemViews,
    taskList: Map<LocalDate, List<KanbanTask>>,
    onTaskListUpdate: (LocalDate, List<KanbanTask>) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        taskList.forEach { (date, taskInfo) ->
            stickyHeader {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .fillMaxWidth()
                        .padding(12.dp),
                )
            }
            itemsIndexed(
                items = taskInfo,
                key = { index, item -> "$index:${item.hashCode()}" }
            ) { index, item ->
                taskViews[item::class]?.let { taskView ->
                    taskView(item) { subInfo ->
                        onTaskListUpdate(date, taskInfo.toMutableList().apply {
                            subInfo?.let { this[index] = it } ?: run { removeAt(index) }
                        })
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun KanbanListPreview() {
    val taskPool = remember {
        mutableStateMapOf<LocalDate, List<KanbanTask>>(*(Array(24) {
            val date = LocalDate.now().plusDays((0..3).random().toLong())
            date to if (Random.nextBoolean()) {
                DefaultKanbanTask(
                    KanbanTaskRange(
                        date.atTime(it, 0, 0) to date.atTime(it, 59, 59),
                        if (Random.nextBoolean())
                            date.atTime(it, 0, 0) to date.atTime(it, 59, 59)
                        else null
                    ),
                    color = generateRandomColor("100"),
                    payload = "Preview - Default"
                )
            } else {
                val randomColor = generateRandomColor("100")
                val maxCapacity = Random.nextInt(4)
                val randomList = Array(maxCapacity) { Random.nextBoolean() }
                NestedKanbanTask(
                    KanbanTaskRange(date.atTime(it, 0, 0) to date.atTime(it, 59, 59)),
                    color = randomColor,
                    payload = "Preview - Nested",
                    children = List(maxCapacity) { index ->
                        NestedKanbanTask.ChildKanbanTask(
                            KanbanTaskRange(
                                date.atTime(index, 0, 0) to date.atTime(index, 59, 59),
                                if (randomList[index])
                                    date.atTime(index, 0, 0) to date.atTime(index, 59, 59)
                                else null
                            ),
                            color = randomColor,
                            payload = "Sub Preview"
                        )
                    }
                )
            }
        }.groupBy({ it.first }) { it.second }.toList().toTypedArray()))
    }

    KanbanList(taskList = taskPool) { time, tasks ->
        if (tasks.isEmpty()) taskPool.remove(time)
        else taskPool[time] = tasks
    }
}