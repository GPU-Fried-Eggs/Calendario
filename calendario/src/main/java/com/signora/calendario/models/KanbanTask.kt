package com.signora.calendario.models

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

interface KanbanTask {
    val range: Pair<LocalDateTime, LocalDateTime>
    val color: Color
    val payload: String
}