package com.signora.calendario.ui.kanban

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.models.DefaultKanbanTask
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.models.NestedKanbanTask
import com.signora.calendario.ui.theme.CalendarTheme
import com.signora.calendario.utils.generateRandomColor
import com.signora.calendario.views.DefaultKanbanTaskView
import com.signora.calendario.views.KanbanTaskView
import com.signora.calendario.views.NestedKanbanTaskView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.reflect.KClass

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KanbanList(
    modifier: Modifier = Modifier,
    taskList: Map<LocalDate, List<Pair<KanbanTask, Boolean?>>>,
    onTaskListUpdate: (LocalDate, List<Pair<KanbanTask, Boolean?>>) -> Unit,
    content: Map<KClass<out KanbanTask>, KanbanTaskView>? = null
) {
    LazyColumn(
        modifier = modifier
            .background(CalendarTheme.colors.backgroundColor)
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
                        .background(CalendarTheme.colors.backgroundColor)
                        .fillMaxWidth()
                        .padding(12.dp),
                )
            }
            items(taskInfo.size) { index ->
                val (masterTask, masterState) = taskInfo[index]

                when (masterTask) {
                    is DefaultKanbanTask -> DefaultKanbanTaskView
                    is NestedKanbanTask -> NestedKanbanTaskView
                    else -> content?.get(masterTask::class)
                }?.let {
                    it(masterTask, masterState) { subTask, subState ->
                        onTaskListUpdate(date, taskInfo.toMutableList().apply {
                            subTask?.let {
                                this[index] = subTask to subState
                            } ?: run {
                                this.removeAt(index)
                            }
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
        mutableStateMapOf<LocalDate, List<Pair<KanbanTask, Boolean?>>>(*(Array(24) {
            val date = LocalDate.now().plusDays((0..3).random().toLong())
            date to if (Random.nextBoolean()) {
                DefaultKanbanTask(
                    range = date.atTime(it, 0, 0) to date.atTime(it, 59, 59),
                    color = generateRandomColor("100"),
                    payload = "Preview"
                ) to Random.nextBoolean()
            } else {
                val randomColor = generateRandomColor("100")
                val maxCapacity = Random.nextInt(4)
                val randomList = Array(maxCapacity) { Random.nextBoolean() }
                NestedKanbanTask(
                    range = date.atTime(it, 0, 0) to date.atTime(it, 59, 59),
                    color = randomColor,
                    payload = "Preview",
                    children = List(maxCapacity) { index ->
                        NestedKanbanTask.ChildKanbanTask(
                            range = date.atTime(index, 0, 0) to date.atTime(index, 59, 59),
                            color = randomColor,
                            payload = "Sub Preview",
                            state = randomList[index]
                        )
                    }
                ) to when {
                    randomList.all { value -> value } -> true
                    randomList.none { value -> value } -> false
                    else -> null
                }
            }
        }.groupBy({ it.first }, { it.second }).toList().toTypedArray()))
    }

    KanbanList(
        taskList = taskPool,
        onTaskListUpdate = { time, tasks -> taskPool[time] = tasks }
    )
}