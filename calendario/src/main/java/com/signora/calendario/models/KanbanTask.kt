package com.signora.calendario.models

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

interface KanbanTask {
    val range: Pair<LocalDateTime, LocalDateTime>
    val color: Color
    val payload: String
}

internal data class DefaultKanbanTask(
    override val range: Pair<LocalDateTime, LocalDateTime>,
    override val color: Color = Color(0xFFFFCDD2),
    override val payload: String,
) : KanbanTask

internal data class NestedKanbanTask(
    override val range: Pair<LocalDateTime, LocalDateTime>,
    override val color: Color = Color(0xFFFFCDD2),
    override val payload: String,
    val child: List<DefaultKanbanTask>
) : KanbanTask