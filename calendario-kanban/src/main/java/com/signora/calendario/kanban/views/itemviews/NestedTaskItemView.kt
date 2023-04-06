package com.signora.calendario.kanban.views.itemviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signora.calendario.kanban.R
import com.signora.calendario.kanban.models.KanbanTask
import com.signora.calendario.kanban.models.KanbanTaskRange
import com.signora.calendario.kanban.models.NestedKanbanTask
import com.signora.calendario.kanban.ui.checkbox.Checkbox
import com.signora.calendario.kanban.ui.checkbox.TriStateCheckbox
import com.signora.calendario.kanban.utils.generateRandomColor
import com.signora.calendario.kanban.views.ContainerBackgroundAlpha
import com.signora.calendario.kanban.views.ContainerBorderWidth
import com.signora.calendario.kanban.views.ContainerPadding
import com.signora.calendario.kanban.views.ContainerRadiusSize
import com.signora.calendario.kanban.views.ContentMaxLines
import com.signora.calendario.kanban.views.ContentMinLines
import com.signora.calendario.kanban.views.ContentPaddingSize
import com.signora.calendario.kanban.views.KanbanTaskView
import java.time.LocalDateTime
import kotlin.random.Random

internal val NestedTaskItemView: KanbanTaskView = { task, onTaskUpdate ->
    val taskStateList = (task as NestedKanbanTask).children.associateWith { it.state }
    val animVisibleState = remember { MutableTransitionState(true) }
    var expand by remember { mutableStateOf(false) }

    LaunchedEffect(animVisibleState.currentState) {
        if (!animVisibleState.currentState) {
            // Log.d("NestedKanbanTaskView", "remove element $task")
            onTaskUpdate(null)
        }
    }

    AnimatedVisibility(
        visibleState = animVisibleState,
        exit = slideOutHorizontally() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = task.color.copy(ContainerBackgroundAlpha),
                    shape = RoundedCornerShape(ContainerRadiusSize)
                )
                .border(
                    width = ContainerBorderWidth,
                    color = task.color,
                    shape = RoundedCornerShape(ContainerRadiusSize)
                )
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContainerPadding)
                    .animateContentSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TriStateCheckbox(
                    state = when (task.state) {
                        true -> ToggleableState.On
                        false -> ToggleableState.Off
                        else -> ToggleableState.Indeterminate
                    },
                    onClick = {
                        onTaskUpdate(task.formatTask(
                            if (taskStateList.isNotEmpty()) taskStateList.none { it.value }
                            else !task.state!!
                        ))
                    },
                    modifier = Modifier.padding(ContentPaddingSize)
                )
                BasicText(
                    text = task.payload,
                    modifier = Modifier
                        .padding(vertical = ContentPaddingSize)
                        .weight(1f)
                        .clickable(MutableInteractionSource(), null) {
                            expand = !expand
                        },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (expand) ContentMaxLines else ContentMinLines
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(role = Role.Button) {
                            animVisibleState.targetState = false
                            // onTaskUpdate(null)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_outline_delete),
                        contentDescription = stringResource(R.string.remove_task_icon_description),
                        modifier = Modifier.padding(ContentPaddingSize)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(
                        start = ContainerPadding + ContentPaddingSize,
                        end = ContainerPadding
                    )
            ) {
                taskStateList.forEach { (subTask, checked) ->
                    var subExpand by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .then(
                                if (subTask.color != task.color) {
                                    Modifier
                                        .background(
                                            color = subTask.color.copy(ContainerBackgroundAlpha),
                                            shape = RoundedCornerShape(ContainerRadiusSize)
                                        )
                                        .border(
                                            width = ContainerBorderWidth,
                                            color = subTask.color,
                                            shape = RoundedCornerShape(ContainerRadiusSize)
                                        )
                                } else Modifier
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked ->
                                onTaskUpdate(task.copy(children = taskStateList.map {
                                    if (it.key == subTask) it.key.formatTask(checked) else it.key
                                }))
                            },
                            modifier = Modifier.padding(ContentPaddingSize),
                            size = 16.dp,
                            cornerRadius = 2.dp
                        )
                        BasicText(
                            text = subTask.payload,
                            modifier = Modifier
                                .padding(vertical = ContentPaddingSize)
                                .clickable(MutableInteractionSource(), null) {
                                    subExpand = !subExpand
                                },
                            overflow = TextOverflow.Ellipsis,
                            maxLines = if (subExpand) ContentMaxLines else ContentMinLines
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NestedTaskItemViewPreview() {
    val defaultChecked = remember { mutableStateListOf(*Array(3) { Random.nextBoolean() }) }
    val randomColor = remember { generateRandomColor("100") }
    val task: MutableState<KanbanTask> = remember {
        mutableStateOf(
            NestedKanbanTask(
                KanbanTaskRange(
                    LocalDateTime.of(2023, 1, 1, 0, 0, 0) to LocalDateTime.of(2023, 1, 1, 0, 59, 59)
                ),
                color = randomColor,
                payload = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                children = List(defaultChecked.size) { index ->
                    NestedKanbanTask.ChildKanbanTask(
                        KanbanTaskRange(
                            LocalDateTime.of(2023, 1, index + 1, 0, 0, 0) to
                                    LocalDateTime.of(2023, 1, index + 1, 0, 59, 59),
                            if (defaultChecked[index])
                                LocalDateTime.of(2023, 1, index + 1, 0, 0, 0) to
                                        LocalDateTime.of(2023, 1, index + 1, 0, 59, 59)
                            else null
                        ),
                        color = randomColor,
                        payload = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
                    )
                }
            )
        )
    }

    NestedTaskItemView(task.value) { newTask -> if (newTask != null) task.value = newTask }
}