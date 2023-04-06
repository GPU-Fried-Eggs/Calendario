package com.signora.calendario.kanban

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import com.signora.calendario.kanban.ui.KanbanHeader
import com.signora.calendario.kanban.ui.KanbanState

@Composable
fun Kanban() {
    val kanbanState = rememberSaveable(saver = KanbanState.Saver) { KanbanState() }

    KanbanHeader(onClick = {})
    // KanbanPager(state = kanbanState)
}