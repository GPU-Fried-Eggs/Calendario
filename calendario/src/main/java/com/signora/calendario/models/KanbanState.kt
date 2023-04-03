package com.signora.calendario.models

import java.time.LocalDateTime

sealed interface KanbanState {
    data class LoadDate(val data: Array<LocalDateTime>) : KanbanState

    object Loading: KanbanState
}