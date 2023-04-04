package com.signora.calendario.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.R
import com.signora.calendario.models.DefaultKanbanTask
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.models.NestedKanbanTask
import com.signora.calendario.ui.theme.CalendarTheme
import com.signora.calendario.utils.generateRandomColor
import java.time.LocalDate
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KanbanListView(
    modifier: Modifier = Modifier,
    taskList: Map<LocalDate, List<Pair<KanbanTask, Boolean?>>>,
    onTaskListUpdate: (LocalDate, List<Pair<KanbanTask, Boolean?>>) -> Unit
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
                    text = date.toString(),
                    modifier = Modifier
                        .background(CalendarTheme.colors.backgroundColor)
                        .fillMaxWidth()
                        .padding(12.dp),
                )
            }
            items(taskInfo.size) { index ->
                val (masterTask, masterState) = taskInfo[index]

                when (masterTask) {
                    is DefaultKanbanTask -> {
                        Row(
                            modifier = Modifier
                                .background(
                                    masterTask.color.copy(alpha = 0.6f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(2.dp, masterTask.color, RoundedCornerShape(8.dp))
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = masterState ?: false,
                                onCheckedChange = {
                                    onTaskListUpdate(date, taskInfo.toMutableList().apply {
                                        this[index] = masterTask to it
                                    })
                                }
                            )
                            Text(text = masterTask.payload)
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    onTaskListUpdate(date, taskInfo.toMutableList().apply {
                                        this.removeAt(index)
                                    })
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_outline_delete),
                                    contentDescription = stringResource(R.string.remove_task_description)
                                )
                            }
                        }
                    }
                    is NestedKanbanTask -> {
                        NestedKanbanView(masterTask, masterState) { subTask, subState ->
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
}

@Composable
private fun NestedKanbanView(task: NestedKanbanTask, state: Boolean?, onTaskUpdate: (NestedKanbanTask?, Boolean?) -> Unit) {
    val taskStateList = task.children.associateWith { it.state }

    Column(
        modifier = Modifier
            .background(task.color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .border(2.dp, task.color, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TriStateCheckbox(
                state = when(state) {
                    true -> ToggleableState.On
                    false -> ToggleableState.Off
                    else -> ToggleableState.Indeterminate
                },
                onClick = {
                    val updated = taskStateList.none { it.value }
                    onTaskUpdate(
                        task.copy(
                            children = taskStateList.map {
                                it.key.copy(state = updated)
                            }.toList()
                        ),
                        updated
                    )
                }
            )
            Text(text = task.payload)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    onTaskUpdate(null, null)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_outline_delete),
                    contentDescription = stringResource(R.string.remove_task_description)
                )
            }
        }

        Column(Modifier.padding(start = 20.dp)) {
            taskStateList.forEach { (subTask, checked) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked ->
                            val updatedChildren = taskStateList.map {
                                if (it.key == subTask) it.key.copy(state = checked) else it.key
                            }
                            onTaskUpdate(
                                task.copy(children = updatedChildren),
                                when {
                                    updatedChildren.all { it.state } -> true
                                    updatedChildren.none { it.state } -> false
                                    else -> null
                                }
                            )
                        }
                    )
                    Text(text = subTask.payload)
                }
            }
        }
    }
}

@Preview
@Composable
private fun KanbanListViewPreview() {
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

    KanbanListView(
        taskList = taskPool,
        onTaskListUpdate = { time, tasks -> taskPool[time] = tasks }
    )
}