package com.signora.calendario.kanban.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.signora.calendario.kanban.models.DefaultKanbanTask
import com.signora.calendario.kanban.models.KanbanTask
import com.signora.calendario.kanban.models.NestedKanbanTask
import com.signora.calendario.kanban.views.itemviews.DefaultTaskItemView
import com.signora.calendario.kanban.views.itemviews.NestedTaskItemView
import kotlin.reflect.KClass

internal const val ContainerBackgroundAlpha = 0.6f
internal const val ContentMaxLines = 8
internal const val ContentMinLines = 2

internal val ContainerPadding = 12.dp
internal val ContainerRadiusSize = 8.dp
internal val ContainerBorderWidth = 2.dp
internal val ContentPaddingSize = 8.dp

typealias KanbanTaskView = @Composable (
    task: KanbanTask,
    onTaskUpdate: (KanbanTask?) -> Unit
) -> Unit

object ItemViewDefaults {
    val taskItemViews: Map<KClass<out KanbanTask>, KanbanTaskView> = mapOf(
        DefaultKanbanTask::class to DefaultTaskItemView,
        NestedKanbanTask::class to NestedTaskItemView
    )
}