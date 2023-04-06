package com.signora.calendario.kanban.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.signora.calendario.kanban.enums.KanbanLayout
import com.signora.calendario.kanban.models.KanbanTask
import com.signora.calendario.kanban.ui.layout.KanbanList
import java.time.LocalDate

@Composable
fun KanbanPager(
    taskList: Map<LocalDate, List<KanbanTask>>,
    onTaskListUpdate: (LocalDate, List<KanbanTask>) -> Unit,
    layout: KanbanLayout = KanbanLayout.GRID,
    onLayoutChange: (KanbanLayout) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        KanbanHeader { onLayoutChange(it) }

        when (layout) {
            KanbanLayout.LIST -> {
                KanbanList(
                    taskList = taskList,
                    onTaskListUpdate = onTaskListUpdate
                )
            }
            KanbanLayout.GRID -> {
                // state.selectedTasks?.let {
                //     KanbanGrid(
                //         timeScalar = (0..23).map {
                //             LocalDateTime.of(2023, 1, 1, it, 0, 0)
                //         },
                //         taskList = it
                //     )
                // } ?: run {
                //     CircularProgressIndicator()
                // }
            }
        }
    }
}

@Preview
@Composable
private fun KanbanPreview() {
    // KanbanPager(state = )
}