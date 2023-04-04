package com.signora.calendario.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
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
fun <T : KanbanTask> KanbanListView(
    modifier: Modifier = Modifier,
    taskList: Map<LocalDate, List<Pair<T, Boolean>>>,
) {
    LazyColumn(
        modifier = modifier
            .background(CalendarTheme.colors.backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        taskList.forEach { (time, state) ->
            stickyHeader {
                Text(
                    text = time.toString(),
                    modifier = Modifier
                        .background(CalendarTheme.colors.backgroundColor)
                        .fillMaxWidth()
                        .padding(12.dp, 12.dp, 12.dp, 4.dp),
                )
            }
            items(state.size) { index ->
                val (task, done) = state[index]

                when (task) {
                    is DefaultKanbanTask -> {
                        Row(
                            modifier = Modifier
                                .background(task.color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .border(2.dp, task.color, RoundedCornerShape(8.dp))
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = done,
                                onCheckedChange = {
                                }
                            )
                            Text(
                                text = task.payload
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_outline_delete),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                    is NestedKanbanTask -> {
                        Column(
                            modifier = Modifier
                                .background(task.color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .border(2.dp, task.color, RoundedCornerShape(8.dp))
                                .fillMaxWidth()
                        ) {
                            val taskState = remember {
                                mutableStateMapOf(*task.child.map { it to false }.toTypedArray())
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TriStateCheckbox(
                                    state = when {
                                        taskState.all { it.value } -> ToggleableState.On
                                        taskState.all { !it.value } -> ToggleableState.Off
                                        else -> ToggleableState.Indeterminate
                                    },
                                    onClick = {}
                                )
                                Text(text = task.payload)
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_outline_delete),
                                        contentDescription = ""
                                    )
                                }
                            }

                            NestedKanban(taskState) { (task, check) ->
                                taskState[task] = check
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T : KanbanTask> NestedKanban(
    state: Map<T, Boolean>,
    onStateUpdate: (Pair<T, Boolean>) -> Unit,
) {
    Column(Modifier.padding(start = 20.dp)) {
        state.forEach { (task, checked) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onStateUpdate(task to it) }
                )
                Text(text = task.payload)
            }
        }
    }
}

@Preview
@Composable
private fun KanbanListViewPreview() {
    val taskPool = remember {
        mutableStateListOf(*Array(24) {
            val date = LocalDate.now().plusDays((0..3).random().toLong())
            date to if (Random.nextBoolean()) {
                DefaultKanbanTask(
                    range = date.atTime(it, 0, 0) to date.atTime(it, 59, 59),
                    color = generateRandomColor("100"),
                    payload = "Preview"
                )
            } else {
                val randomColor = generateRandomColor("100")
                NestedKanbanTask(
                    range = date.atTime(it, 0, 0) to date.atTime(it, 59, 59),
                    color = randomColor,
                    payload = "Preview",
                    child = listOf(
                        DefaultKanbanTask(
                            range = date.atTime(it, 0, 0) to date.atTime(it, 59, 59),
                            color = randomColor,
                            payload = "Sub Preview"
                        )
                    )
                )
            }
        })
    }

    KanbanListView(
        taskList = taskPool.groupBy({ it.first }, { it.second to Random.nextBoolean() })
    )
}