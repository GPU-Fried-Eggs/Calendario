package com.signora.calendario.models

import java.time.LocalDate

sealed interface KanbanIntent {
    data class LoadTask(val date: LocalDate, val range: Int = 1, val period: KanbanPeriod = KanbanPeriod.DAY): KanbanIntent

    object UseGridLayout: KanbanIntent

    object UseListLayout: KanbanIntent
}