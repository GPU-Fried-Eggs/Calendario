package com.signora.calendario.models

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

data class Task(
    val range: Pair<LocalDateTime, LocalDateTime>,
    val color: Color = Color(0xFFB2EBF2),
    val payload: String,
    val children: List<Task>? = null
)