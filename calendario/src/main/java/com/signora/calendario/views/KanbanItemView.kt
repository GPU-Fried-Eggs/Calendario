package com.signora.calendario.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.R
import com.signora.calendario.models.DefaultKanbanTask
import com.signora.calendario.models.KanbanTask
import com.signora.calendario.models.NestedKanbanTask
import com.signora.calendario.utils.generateRandomColor
import java.time.LocalDateTime
import kotlin.random.Random

typealias KanbanTaskView = @Composable (
    task: KanbanTask,
    state: Boolean?,
    onTaskUpdate: (KanbanTask?, Boolean?) -> Unit,
) -> Unit

private const val MIN_LINES = 2
private const val MAX_LINES = 8

internal val DefaultKanbanTaskView: KanbanTaskView = { task, state, onTaskUpdate ->
    // val animVisibleState = remember { MutableTransitionState(true) }
    var expand by remember { mutableStateOf(false) }

    // LaunchedEffect(animVisibleState.currentState) {
    //     if (!animVisibleState.currentState) {
    //         Log.d("DefaultKanbanTaskView", "remove element $task")
    //         onTaskUpdate(null, null)
    //     }
    // }

    // AnimatedVisibility(
    //     visibleState = animVisibleState,
    //     exit = slideOutHorizontally() + fadeOut()
    // ) {
    Row(
        modifier = Modifier
            .background(task.color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .border(2.dp, task.color, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = state ?: false,
            onCheckedChange = { onTaskUpdate(task, it) }
        )
        Text(
            text = task.payload,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f)
                .clickable(MutableInteractionSource(), null) {
                    expand = !expand
                },
            overflow = TextOverflow.Ellipsis,
            maxLines = if (expand) MAX_LINES else MIN_LINES
        )
        IconButton(
            onClick = {
                // animVisibleState.targetState = false
                onTaskUpdate(null, null)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_outline_delete),
                contentDescription = stringResource(R.string.remove_task_icon_description)
            )
        }
    }
    // }
}

internal val NestedKanbanTaskView: KanbanTaskView = { task, state, onTaskUpdate ->
    val taskStateList = (task as NestedKanbanTask).children.associateWith { it.state }
    // val animVisibleState = remember { MutableTransitionState(true) }
    var expand by remember { mutableStateOf(false) }

    // LaunchedEffect(animVisibleState.currentState) {
    //     if (!animVisibleState.currentState) {
    //         Log.d("NestedKanbanTaskView", "remove element $task")
    //         onTaskUpdate(null, null)
    //     }
    // }

    // AnimatedVisibility(
    //     visibleState = animVisibleState,
    //     exit = slideOutHorizontally() + fadeOut()
    // ) {
    Column(
        modifier = Modifier
            .background(task.color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .border(2.dp, task.color, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TriStateCheckbox(
                state = when (state) {
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
            Text(
                text = task.payload,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
                    .clickable(MutableInteractionSource(), null) {
                        expand = !expand
                    },
                overflow = TextOverflow.Ellipsis,
                maxLines = if (expand) MAX_LINES else MIN_LINES
            )
            IconButton(
                onClick = {
                    // animVisibleState.targetState = false
                    onTaskUpdate(null, null)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_outline_delete),
                    contentDescription = stringResource(R.string.remove_task_icon_description)
                )
            }
        }

        Column(
            modifier = Modifier.padding(start = 20.dp, end = 12.dp)
        ) {
            taskStateList.forEach { (subTask, checked) ->
                var subExpand by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .fillMaxWidth()
                        .then(
                            if (subTask.color != task.color) {
                                Modifier
                                    .background(
                                        subTask.color.copy(alpha = 0.6f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(2.dp, subTask.color, RoundedCornerShape(8.dp))
                            } else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    Text(
                        text = subTask.payload,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable(MutableInteractionSource(), null) {
                                subExpand = !subExpand
                            },
                        overflow = TextOverflow.Ellipsis,
                        maxLines = if (subExpand) 8 else 2
                    )
                }
            }
        }
    }
    // }
}

@Preview
@Composable
private fun DefaultKanbanTaskViewPreview() {
    val checked: MutableState<Boolean?> = remember { mutableStateOf(true) }

    DefaultKanbanTaskView(
        DefaultKanbanTask(
            Pair(
                LocalDateTime.of(2023, 1, 1, 0, 0, 0),
                LocalDateTime.of(2023, 1, 1, 0, 59, 59)
            ),
            color = generateRandomColor("100"),
            payload = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lectus sit amet est placerat in egestas. Sodales ut eu sem integer vitae justo eget. Nibh nisl condimentum id venenatis a condimentum vitae. Tortor posuere ac ut consequat semper viverra. Lectus urna duis convallis convallis. Sodales ut etiam sit amet nisl purus. Facilisis gravida neque convallis a cras semper auctor. Quis blandit turpis cursus in hac. Et sollicitudin ac orci phasellus egestas. Dignissim enim sit amet venenatis urna. Posuere ac ut consequat semper viverra nam libero justo laoreet. Diam vulputate ut pharetra sit amet aliquam. Mauris cursus mattis molestie a iaculis at erat pellentesque."
        ),
        state = checked.value,
        onTaskUpdate = { _, state -> checked.value = state }
    )
}

@Preview
@Composable
private fun NestedKanbanTaskViewPreview() {
    val defaultChecked = remember { mutableStateListOf(*Array(3) { Random.nextBoolean() }) }
    val randomColor = remember { generateRandomColor("100") }
    val task = remember {
        mutableStateOf<Pair<KanbanTask, Boolean?>>(
            NestedKanbanTask(
                Pair(
                    LocalDateTime.of(2023, 1, 1, 0, 0, 0),
                    LocalDateTime.of(2023, 1, 1, 0, 59, 59)
                ),
                color = randomColor,
                payload = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lectus sit amet est placerat in egestas. Sodales ut eu sem integer vitae justo eget. Nibh nisl condimentum id venenatis a condimentum vitae. Tortor posuere ac ut consequat semper viverra. Lectus urna duis convallis convallis. Sodales ut etiam sit amet nisl purus. Facilisis gravida neque convallis a cras semper auctor. Quis blandit turpis cursus in hac. Et sollicitudin ac orci phasellus egestas. Dignissim enim sit amet venenatis urna. Posuere ac ut consequat semper viverra nam libero justo laoreet. Diam vulputate ut pharetra sit amet aliquam. Mauris cursus mattis molestie a iaculis at erat pellentesque.",
                children = List(defaultChecked.size) { index ->
                    NestedKanbanTask.ChildKanbanTask(
                        Pair(
                            LocalDateTime.of(2023, 1, index + 1, 0, 0, 0),
                            LocalDateTime.of(2023, 1, index + 1, 0, 59, 59)
                        ),
                        color = randomColor,
                        payload = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lectus sit amet est placerat in egestas. Sodales ut eu sem integer vitae justo eget. Nibh nisl condimentum id venenatis a condimentum vitae. Tortor posuere ac ut consequat semper viverra. Lectus urna duis convallis convallis. Sodales ut etiam sit amet nisl purus. Facilisis gravida neque convallis a cras semper auctor. Quis blandit turpis cursus in hac. Et sollicitudin ac orci phasellus egestas. Dignissim enim sit amet venenatis urna. Posuere ac ut consequat semper viverra nam libero justo laoreet. Diam vulputate ut pharetra sit amet aliquam. Mauris cursus mattis molestie a iaculis at erat pellentesque.",
                        state = defaultChecked[index]
                    )
                }
            ) to when {
                defaultChecked.all { value -> value } -> true
                defaultChecked.none { value -> value } -> false
                else -> null
            }
        )
    }

    NestedKanbanTaskView(
        task.value.first,
        state = task.value.second,
        onTaskUpdate = { subTask, subState -> task.value = subTask!! to subState }
    )
}