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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.signora.calendario.kanban.R
import com.signora.calendario.kanban.models.DefaultKanbanTask
import com.signora.calendario.kanban.models.KanbanTask
import com.signora.calendario.kanban.models.KanbanTaskRange
import com.signora.calendario.kanban.ui.checkbox.Checkbox
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

internal val DefaultTaskItemView: KanbanTaskView = { task, onTaskUpdate ->
    val animVisibleState = remember { MutableTransitionState(true) }
    var expand by remember { mutableStateOf(false) }

    LaunchedEffect(animVisibleState.currentState) {
        if (!animVisibleState.currentState) {
            // Log.d("DefaultKanbanTaskView", "remove element $task")
            onTaskUpdate(null)
        }
    }

    AnimatedVisibility(
        visibleState = animVisibleState,
        exit = slideOutHorizontally() + fadeOut()
    ) {
        Row(
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
                .padding(horizontal = ContainerPadding)
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = (task as DefaultKanbanTask).state,
                onCheckedChange = { onTaskUpdate(task.formatTask(it)) },
                modifier = Modifier.padding(ContentPaddingSize)
            )
            BasicText(
                text = task.payload,
                modifier = Modifier
                    .padding(vertical = ContainerPadding)
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
    }
}

@Preview
@Composable
private fun DefaultTaskItemViewPreview() {
    val task: MutableState<KanbanTask> = remember {
        mutableStateOf(
            DefaultKanbanTask(
                KanbanTaskRange(
                    LocalDateTime.of(2023, 1, 1, 0, 0, 0) to LocalDateTime.of(2023, 1, 1, 0, 59, 59)
                ),
                color = generateRandomColor("100"),
                payload = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            )
        )
    }

    DefaultTaskItemView(task.value) { newTask -> if (newTask != null) task.value = newTask }
}