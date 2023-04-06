package com.signora.calendario.kanban.models

import java.time.LocalDate

sealed interface KanbanIntent {
    data class LoadTask(val date: LocalDate, val tasks: List<KanbanTask>): KanbanIntent

    object UseGridLayout: KanbanIntent

    object UseListLayout: KanbanIntent
}