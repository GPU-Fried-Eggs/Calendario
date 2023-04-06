package com.signora.calendario.kanban.models

import java.time.LocalDateTime

sealed interface KanbanRenderState {
    data class Rendering(val data: Array<LocalDateTime>) : KanbanRenderState

    object Error: KanbanRenderState

    object Loading: KanbanRenderState
}